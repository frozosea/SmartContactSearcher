import json
import time
import requests

# Base URL of your FastAPI application
BASE_URL = "http://localhost:8000"

# Headers to be sent with every request (including user_id to satisfy the rate limiter)
HEADERS = {
    "Content-Type": "application/json",
    "X-User-Id": "400"
}

# Path to the contacts JSON file
CONTACTS_FILE = "/Users/frozo/PycharmProjects/external-contact-searcher/tests/data/data_without_embs.json"


def test_healthcheck():
    url = f"{BASE_URL}/healthcheck/health"
    response = requests.get(url, headers=HEADERS)
    print("Healthcheck response:", response.json())
    assert response.status_code == 200, f"Healthcheck failed with status {response.status_code}"
    assert response.json().get("status") == "healthy", "ElasticSearch is not healthy"


def load_contacts():
    with open(CONTACTS_FILE, "r") as f:
        contacts = json.load(f)
    return contacts


def test_upload_contacts(contacts):
    print("\nUploading contacts:")
    for contact in contacts:
        url = f"{BASE_URL}/contact/create"
        response = requests.post(url, json=contact, headers=HEADERS)
        print(f"Uploaded contact {contact['contactId']} - {contact['name']}: {response.json()}")
        time.sleep(0.1)  # Small delay between requests (optional)


def test_search_queries():
    print("\nTesting search queries:")
    test_cases = [
        ("make call with client", "Bob Johnson"),
        ("make reel in instagram", "Ivy Lee"),
        ("start marketing campaign", "Alice Smith"),
    ]
    for query, expected_name in test_cases:
        url = f"{BASE_URL}/contact/search"
        payload = {
            "user_id": "400",
            "query_text": query,
            "k": 5,
            "num_candidates": 100
        }
        response = requests.post(url, json=payload, headers=HEADERS)
        resp_json = response.json()
        print(f"Search query '{query}':", resp_json)
        assert response.status_code == 200, f"Search failed for query '{query}'"
        assert len(resp_json) > 0, f"No results returned for query '{query}'"
        first_contact = resp_json[0]
        assert first_contact.get("name") == expected_name, (
            f"Expected first contact name '{expected_name}', got '{first_contact.get('name')}'"
        )


def test_update_contacts(contacts):
    print("\nUpdating contacts:")
    for contact in contacts:
        # Modify the notesCombined field (or any other field to simulate an update)
        updated_contact = contact.copy()
        updated_contact["notesCombined"] = "Updated: " + updated_contact["notesCombined"]

        url = f"{BASE_URL}/contact/"
        response = requests.put(url, json=updated_contact, headers=HEADERS)
        print(f"Updated contact {contact['contactId']} - {contact['name']}: {response.json()}")
        assert response.status_code == 200, f"Update failed for contact {contact['contactId']}"
        assert "Contact updated successfully" in response.json().get("message", "")
        time.sleep(0.1)


def test_delete_contacts(contacts):
    print("\nDeleting contacts:")
    for contact in contacts:
        url = f"{BASE_URL}/contact/"
        payload = {
            "user_id": contact["userId"],
            "contactId": contact["contactId"]
        }
        response = requests.delete(url, json=payload, headers=HEADERS)
        print(f"Deleted contact {contact['contactId']} - {contact['name']}: {response.json()}")
        assert response.status_code == 200, f"Delete failed for contact {contact['contactId']}"
        time.sleep(0.1)


if __name__ == "__main__":
    print("Starting external tests...\n")
    test_healthcheck()
    contacts = load_contacts()
    test_upload_contacts(contacts)
    time.sleep(1)
    test_search_queries()
    test_update_contacts(contacts)
    test_delete_contacts(contacts)
    print("\nAll tests completed successfully.")
