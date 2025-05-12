import json
from tests.util.embeddings import generate_embeddings


def update_index_mapping(elastic_client, index_name: str):
    """Update the index mapping for Elasticsearch."""
    # Step 1: Delete the existing index (if it exists)
    try:
        if elastic_client.indices.exists(index=index_name):
            elastic_client.indices.delete(index=index_name)
            print(f"Index {index_name} deleted successfully.")
    except Exception as e:
        print(f"Error deleting index: {e}")

    # Step 2: Define the new mapping with 'dense_vector' for embedding field
    new_mapping = {
        "mappings": {
            "properties": {
                "contactId": {
                    "type": "text"
                },
                "embedding": {
                    "type": "dense_vector",
                    "dims": 768,
                    "index": True
                },
                "jobTitle": {
                    "type": "text"
                },
                "name": {
                    "type": "text"
                },
                "notesCombined": {
                    "type": "text"
                },
                "tags": {
                    "type": "text"
                },
                "userId": {
                    "type": "text"
                }
            }
        }
    }

    # Step 3: Create the index with the new mapping
    try:
        elastic_client.indices.create(index=index_name, body=new_mapping)
        print(f"Index {index_name} created with new mapping.")
    except Exception as e:
        print(f"Error creating index with new mapping: {e}")


def _load_data():
    with open("/Users/frozo/PycharmProjects/external-contact-searcher/tests/data/data_embs.json", "r") as file:
        data = json.load(file)
    return data


def preload_data(elastic_client, index_name):
    update_index_mapping(elastic_client, index_name)
    """Load test data into Elasticsearch."""
    generate_embeddings()
    data = _load_data()
    # Index the data
    for doc in data:
        elastic_client.index(index=index_name, id=doc["contactId"], document=doc)
        print(f"Indexed document with ID {doc['contactId']}")
        elastic_client.indices.refresh(index=index_name)

    doc_count = elastic_client.count(index=index_name)
    print(f"Document count: {doc_count['count']}")
