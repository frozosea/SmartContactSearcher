from typing import List, Dict, Any
from app.repository.elastic_repository import IElasticRepository
from app.repository.embedding_repository import IEmbeddingsRepository
from app.service.base_service import BaseService
from app.util.logger import get_logger


class SearchService(BaseService):
    def __init__(self, elastic_repository: IElasticRepository, embedding_repository: IEmbeddingsRepository):
        self.elastic_repository = elastic_repository
        self.embedding_repository = embedding_repository
        self.logger = get_logger()

    def search_contacts(self, user_id: str, query_text: str, k: int = 10, num_candidates: int = 100) -> List[
        Dict[str, Any]]:
        """
        Perform a semantic search for contacts using a query text.

        :param user_id: The user performing the search (used for filtering results).
        :param query_text: The search text for generating embeddings.
        :param k: Number of top results to return.
        :param num_candidates: Number of candidates for kNN search.
        :return: A list of matching documents.
        """
        try:
            # Generate embedding for the query text
            query_vector = self.embedding_repository.generate_embedding(query_text)
            # Build the search body
            search_body = {
                "size": k,
                "_source": True,
                "knn": {
                    "field": "embedding",
                    "query_vector": query_vector,
                    "k": k,
                    "num_candidates": num_candidates
                },
                "query": {
                    "bool": {
                        "filter": [
                            {"term": {"userId": str(user_id)}}  # Filter by userId
                        ]
                    }
                }
            }
            results = self.elastic_repository.search(search_body)
            self.logger.info(f"Search completed. Found {len(results)} results for user {user_id}.")
            return results
        except Exception as e:
            self.logger.error(f"Error in search_contacts: {str(e)}")
            raise

    def search_by_tags(self, user_id: str, tags: List[str]) -> List[Dict[str, Any]]:
        """
        Perform a search for contacts based on tags.

        :param user_id: The user performing the search.
        :param tags: A list of tags to search for.
        :return: A list of matching documents.
        """
        try:
            # Build the search body for tags
            search_body = {
                "query": {
                    "bool": {
                        "must": [
                            {"term": {"userId": user_id}},
                            {"terms": {"tags": tags}}
                        ]
                    }
                }
            }

            # Execute the search query
            results = self.elastic_repository.search(search_body)
            self.logger.info(f"Tag-based search completed. Found {len(results)} results for user {user_id}.")
            return results
        except Exception as e:
            self.logger.error(f"Error in search_by_tags: {str(e)}")
            raise

    def _check_contact_exists(self, contact_id) -> bool:
        contact = self.elastic_repository.get_contact(contact_id)
        print(contact)
        if contact:
            return True
        return False

    def save_new_contact(self, contact_body):
        if not self._check_contact_exists(contact_body["contactId"]):
            combined_text = " ".join(
                [contact_body["name"], contact_body["jobTitle"], " ".join(contact_body["tags"]),
                 contact_body["notesCombined"]])
            contact_body["embedding"] = self.embedding_repository.generate_embedding(combined_text)
            self.elastic_repository.index_contact(contact_body["contactId"], contact_body)
            self.logger.info(f"Contact saved: {contact_body}")
        raise Exception("Contact already exists!")

    def update_contact_info(self, contact_body):
        combined_text = " ".join(
            [contact_body["name"], contact_body["jobTitle"], " ".join(contact_body["tags"]),
             contact_body["notesCombined"]])
        contact_body["embedding"] = self.embedding_repository.generate_embedding(combined_text)
        self.elastic_repository.update_contact(contact_body["contactId"], contact_body)
        self.logger.info(f"Contact updated: {contact_body}")

    def delete_contact(self, contact_id):
        self.elastic_repository.delete_contact(contact_id)
        self.logger.info(f"Contact updated: {contact_id}")
