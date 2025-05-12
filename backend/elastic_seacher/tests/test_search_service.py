import unittest

import dotenv
from elasticsearch import Elasticsearch

from app.service.search_service import SearchService
from app.repository.embedding_repository import OfflineEmbeddingRepository
from app.repository.elastic_repository import ElasticRepository
from tests.util.preload_data import preload_data
from app.util.config import config


class TestSearchService(unittest.TestCase):
    def setUp(self):
        dotenv.load_dotenv()
        print("STARTING...")
        self.elastic_repository = ElasticRepository()
        self.service = SearchService(elastic_repository=self.elastic_repository,
                                     embedding_repository=OfflineEmbeddingRepository(self.elastic_repository))

        self.elastic = Elasticsearch(
            hosts=[{"host": "localhost", "port": config.ELASTICSEARCH_PORT}],
            scheme="http",
            verify_certs=False
        )
        self.elastic.ping()
        preload_data(self.elastic, "test_index")

    def test_search_by_text(self):
        query = "make a reel in instagram"
        results = self.service.search_contacts("400", query, 10, 100)
        self.assertTrue(len(results) > 0)
        i = 0
        for item in results:
            print(f"{i}. Name: {item['name']} jobTitle: {item['jobTitle']} tags: {item['tags']}")
            i += 1

