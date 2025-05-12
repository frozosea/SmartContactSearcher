import unittest
from app.service.embedding_service import EmbeddingService
from tests.mock.mock_embeddings_repository import MockEmbeddingsRepository

class TestEmbeddingService(unittest.TestCase):
    def setUp(self):
        self.embeddings_repository = MockEmbeddingsRepository()
        self.service = EmbeddingService(embeddings_repo=self.embeddings_repository)

    def test_generate_embedding(self):
        text = "Test input"
        embedding = self.service.generate_embedding(text)
        self.assertEqual(embedding, [0.1, 0.2, 0.3, 0.4, 0.5])

    def test_store_embedding(self):
        document_id = "123"
        embedding = [0.1, 0.2, 0.3, 0.4, 0.5]
        metadata = {"name": "Test"}
        self.service.store_embedding(document_id, embedding, metadata)

    def test_get_embedding(self):
        document_id = "123"
        embedding_data = self.service.get_embedding(document_id)
        self.assertIn("embedding", embedding_data)
        self.assertEqual(embedding_data["embedding"], [0.1, 0.2, 0.3, 0.4, 0.5])
