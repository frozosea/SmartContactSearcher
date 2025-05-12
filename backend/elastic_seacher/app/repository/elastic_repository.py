from typing import Dict, Any, Optional, List
from app.repository.base_repository import BaseRepository
from app.util.config import config

from app.util.logger import get_logger


class IElasticRepository:
    def index_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Index a contact document in Elasticsearch."""
        pass

    def get_contact(self, contact_id: str) -> Optional[Dict[str, Any]]:
        """Retrieve a contact document from Elasticsearch."""
        pass

    def update_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Update a contact document in Elasticsearch."""
        pass

    def delete_contact(self, contact_id: str) -> None:
        """Delete a contact document from Elasticsearch."""
        pass

    def search(self, search_body: Dict[str, Any]) -> List[Dict[str, Any]]:
        """Search in Elastic."""
        pass


class ElasticRepository(BaseRepository, IElasticRepository):
    def __init__(self):
        super().__init__()
        self.index_name = config.ELASTICSEARCH_INDEX_NAME

    def index_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Index a contact document in Elasticsearch."""
        self.index_document(index=self.index_name, document_id=contact_id, document=document)
        self.client.indices.refresh(index=self.index_name)

    def get_contact(self, contact_id: str) -> Optional[Dict[str, Any]]:
        """Retrieve a contact document from Elasticsearch."""
        return self.get_document(index=self.index_name, document_id=contact_id)

    def update_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Update a contact document in Elasticsearch."""
        self.update_document(index=self.index_name, document_id=contact_id, document=document)
        self.client.indices.refresh(index=self.index_name)

    def delete_contact(self, contact_id: str) -> None:
        """Delete a contact document from Elasticsearch."""
        self.delete_document(index=self.index_name, document_id=contact_id)
        self.client.indices.refresh(index=self.index_name)

    def search(self, search_body: Dict[str, Any]) -> List[Dict[str, Any]]:
        """
        Perform a search query in Elasticsearch.

        :param search_body: The body of the search query.
        :return: A list of matching documents.
        """
        logger = get_logger()
        logger.debug(f"Executing search on index {self.index_name} with query: {search_body}")
        try:
            response = self.client.search(index=self.index_name, body=search_body)
            hits = response.get("hits", {}).get("hits", [])
            results = [hit["_source"] for hit in hits]
            logger.info(f"Search returned {len(results)} results.")
            return results
        except Exception as e:
            logger.error(f"Error executing search: {e}")
            raise
