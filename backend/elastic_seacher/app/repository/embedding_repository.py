import sys
from abc import ABC, abstractmethod
from typing import List, Dict
import requests
from app.util.logger import get_logger
from sentence_transformers import SentenceTransformer

from app.util.config import config
from app.repository.elastic_repository import IElasticRepository


class IEmbeddingsRepository(ABC):
    @abstractmethod
    def generate_embedding(self, text: str) -> List[float]:
        """Generate an embedding for the given text."""
        pass

    @abstractmethod
    def store_embedding(self, document_id: str, embedding: List[float], metadata: Dict[str, any]) -> None:
        """Store an embedding in Elasticsearch."""
        pass

    @abstractmethod
    def get_embedding(self, document_id: str) -> Dict:
        """Retrieve embedding metadata from Elasticsearch."""
        pass


class EmbeddingsRepository(IEmbeddingsRepository):
    def __init__(self, elastic_repo: IElasticRepository):
        self.api_url = config.OPENAI_API_URL
        self.api_key = config.OPENAI_API_KEY
        self.elastic_repo = elastic_repo
        self.logger = get_logger()

    def generate_embedding(self, text: str) -> List[float]:
        """Call OpenAI API to generate embeddings for the given text."""
        self.logger.debug(f"Generating embedding for text: {text}")
        headers = {"Authorization": f"Bearer {self.api_key}"}
        payload = {
            "model": "text-embedding-ada-002",  # OpenAI model
            "input": text
        }
        response = requests.post(self.api_url, headers=headers, json=payload)
        if response.status_code != 200:
            self.logger.error(f"Error generating embedding: {response.json()}")
            raise Exception(f"Failed to generate embedding: {response.text}")
        embedding = response.json().get("data")[0].get("embedding")
        self.logger.debug(f"Generated embedding: {embedding[:10]}...")  # Log first 10 floats for brevity
        return embedding

    def store_embedding(self, document_id: str, embedding: List[float], metadata: Dict[str, any]) -> None:
        """Store embedding in Elasticsearch."""
        self.logger.debug(f"Storing embedding for document ID: {document_id}")
        document = {
            **metadata,
            "embedding": embedding
        }
        self.elastic_repo.index_document(document_id=document_id, document=document)

    def get_embedding(self, document_id: str) -> Dict:
        """Retrieve embedding from Elasticsearch."""
        self.logger.debug(f"Retrieving embedding for document ID: {document_id}")
        document = self.elastic_repo.get_document(document_id)
        if not document:
            self.logger.warning(f"No embedding found for document ID: {document_id}")
            return None
        self.logger.debug(f"Retrieved embedding for document ID: {document_id}")
        return document


class OfflineEmbeddingRepository(IEmbeddingsRepository):
    def __init__(self, elastic_repository):
        self.model = SentenceTransformer(f"{sys.path[1]}/app/nn_model/all-mpnet-base-v2")
        self.elastic_repo = elastic_repository
        self.logger = get_logger()

    def generate_embedding(self, text: str) -> List[float]:
        self.logger.debug(f"Generating embedding for text: {text}")
        embedding = self.model.encode(text).tolist()
        self.logger.debug(f"Generated embedding: {embedding[:10]}...")  # Log first 10 floats for brevity
        return embedding

    def store_embedding(self, document_id: str, embedding: List[float], metadata: Dict[str, any]) -> None:
        """Store embedding in Elasticsearch."""
        self.logger.debug(f"Storing embedding for document ID: {document_id}")
        document = {
            **metadata,
            "embedding": embedding
        }
        self.elastic_repo.index_document(document_id=document_id, document=document)

    def get_embedding(self, document_id: str) -> Dict:
        """Retrieve embedding from Elasticsearch."""
        self.logger.debug(f"Retrieving embedding for document ID: {document_id}")
        document = self.elastic_repo.get_document(document_id)
        if not document:
            self.logger.warning(f"No embedding found for document ID: {document_id}")
            return None
        self.logger.debug(f"Retrieved embedding for document ID: {document_id}")
        return document

