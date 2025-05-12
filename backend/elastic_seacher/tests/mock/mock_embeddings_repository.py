from typing import List, Dict
from app.repository.embedding_repository import IEmbeddingsRepository


class MockEmbeddingsRepository(IEmbeddingsRepository):
    def generate_embedding(self, text: str) -> List[float]:
        """Return a fixed embedding for testing."""
        return [0.1, 0.2, 0.3, 0.4, 0.5]  # Predefined embedding

    def store_embedding(self, document_id: str, embedding: List[float], metadata: Dict[str, any]) -> None:
        """Mock storing an embedding."""
        print(f"Mock store embedding: {document_id}")

    def get_embedding(self, document_id: str) -> Dict:
        """Return a fixed document for testing."""
        return {"id": document_id, "embedding": [0.1, 0.2, 0.3, 0.4, 0.5], "metadata": {"name": "Test"}}
