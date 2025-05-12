from abc import ABC, abstractmethod
from typing import Dict, Any, Optional
from elasticsearch import Elasticsearch, NotFoundError
from app.util.logger import get_logger
from app.util.config import config


class IBaseRepository(ABC):
    @abstractmethod
    def index_document(self, index: str, document_id: str, document: Dict[str, Any]) -> None:
        """Index a document in Elasticsearch."""
        pass

    @abstractmethod
    def get_document(self, index: str, document_id: str) -> Optional[Dict[str, Any]]:
        """Retrieve a document from Elasticsearch."""
        pass

    @abstractmethod
    def update_document(self, index: str, document_id: str, document: Dict[str, Any]) -> None:
        """Update a document in Elasticsearch."""
        pass

    @abstractmethod
    def delete_document(self, index: str, document_id: str) -> None:
        """Delete a document from Elasticsearch."""
        pass


class BaseRepository(IBaseRepository):
    def __init__(self):
        self.client = Elasticsearch(
            hosts=[{"host": config.ELASTIC_HOST, "port": config.ELASTICSEARCH_PORT}],
            # basic_auth=("elastic", config.ELASTICSEARCH_PASSWORD),
            scheme="http",
            verify_certs=False
        )
        self.logger = get_logger()
        self.logger.debug("Elasticsearch client initialized.")

    def index_document(self, index: str, document_id: str, document: Dict[str, Any]) -> None:
        """Index a document in Elasticsearch."""
        self.logger.debug(f"Indexing document in {index} with ID {document_id}.")
        try:
            self.client.index(index=index, id=document_id, document=document)
            self.logger.info(f"Document indexed in {index} with ID {document_id}.")
        except Exception as e:
            self.logger.error(f"Error indexing document: {e}")
            raise

    def get_document(self, index: str, document_id: str) -> Optional[Dict[str, Any]]:
        """Retrieve a document from Elasticsearch."""
        self.logger.debug(f"Retrieving document from {index} with ID {document_id}.")
        try:
            response = self.client.get(index=index, id=document_id)
            return response.get("_source")
        except NotFoundError:
            self.logger.warning(f"Document not found in {index} with ID {document_id}.")
            return None
        except Exception as e:
            self.logger.error(f"Error retrieving document: {e}")
            raise

    def update_document(self, index: str, document_id: str, document: Dict[str, Any]) -> None:
        """Update a document in Elasticsearch."""
        self.logger.debug(f"Updating document in {index} with ID {document_id}.")
        try:
            self.client.update(index=index, id=document_id, body={"doc": document})
            self.logger.info(f"Document updated in {index} with ID {document_id}.")
        except Exception as e:
            self.logger.error(f"Error updating document: {e}")
            raise

    def delete_document(self, index: str, document_id: str) -> None:
        """Delete a document from Elasticsearch."""
        self.logger.debug(f"Deleting document from {index} with ID {document_id}.")
        try:
            self.client.delete(index=index, id=document_id)
            self.logger.info(f"Document deleted from {index} with ID {document_id}.")
        except NotFoundError:
            self.logger.warning(f"Document not found in {index} with ID {document_id}.")
        except Exception as e:
            self.logger.error(f"Error deleting document: {e}")
            raise
