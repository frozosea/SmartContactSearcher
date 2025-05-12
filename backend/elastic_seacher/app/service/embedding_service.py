from typing import List, Dict, Any
from app.service.base_service import BaseService
from app.repository.embedding_repository import IEmbeddingsRepository



class EmbeddingService(BaseService):
    """
    Service for handling embedding generation and management.
    """

    def __init__(self, embedding_repository: IEmbeddingsRepository):
        super().__init__()
        self.embedding_repository = embedding_repository

    def generate_embedding(self, text: str) -> List[float]:
        """
        Generates an embedding vector for a given text using the external API.

        :param text: Input text to generate the embedding for.
        :return: A list representing the embedding vector.
        """
        try:
            embedding = self.embedding_repository.generate_embedding(text)
            if not embedding:
                raise ValueError("No embedding found in the response.")
            self.logger.info("Embedding generated successfully.")
            return embedding
        except Exception as e:
            self.logger.error(f"Error generating embedding: {str(e)}")
            raise

    def index_embedding(self, document_id: str, user_id: str, embedding_data: Dict[str, Any]) -> None:
        """
        Stores an embedding document in Elasticsearch.

        :param document_id: Unique identifier for the document.
        :param user_id: Identifier of the user who owns the document.
        :param embedding_data: The embedding and associated metadata.
        """
        try:
            document = {
                "contactId": document_id,
                "userId": user_id,
                "name": embedding_data.get("name"),
                "jobTitle": embedding_data.get("jobTitle"),
                "tags": embedding_data.get("tags", []),
                "notesCombined": embedding_data.get("notesCombined", ""),
                "embedding": embedding_data.get("embedding"),
            }
            self.elastic_repository.index_document(document_id, document)
            self.logger.info(f"Embedding for document {document_id} indexed successfully.")
        except Exception as e:
            self.logger.error(f"Error indexing embedding: {str(e)}")
            raise

    def search_by_embedding(self, query_vector: List[float], user_id: str, k: int = 10, num_candidates: int = 100) -> \
    List[Dict[str, Any]]:
        """
        Performs a semantic search using a query vector in Elasticsearch.

        :param query_vector: The embedding vector for the search query.
        :param user_id: Identifier of the user performing the search.
        :param k: Number of top results to return.
        :param num_candidates: Number of candidates for kNN search.
        :return: A list of matching documents with metadata.
        """
        try:
            search_body = {
                "knn": {
                    "field": "embedding",
                    "query_vector": query_vector,
                    "k": k,
                    "num_candidates": num_candidates
                },
                "_source": True,
                "query": {
                    "term": {"userId": user_id}
                }
            }
            results = self.elastic_repository.search(search_body)
            self.logger.info(f"Search completed. Found {len(results)} results.")
            return results
        except Exception as e:
            self.logger.error(f"Error in search_by_embedding: {str(e)}")
            raise
