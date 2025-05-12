from typing import Dict, List, Optional


class MockElasticRepository:
    def __init__(self):
        self.data = {}

    def index_document(self, index: str, document_id: str, document: Dict) -> None:
        """Mock index a document."""
        self.data[document_id] = document

    def get_document(self, index: str, document_id: str) -> Optional[Dict]:
        """Mock retrieve a document."""
        return self.data.get(document_id)

    def search(self, index: str, query: Dict) -> List[Dict]:
        """Mock a search operation."""
        return [{"_id": doc_id, "_source": doc} for doc_id, doc in self.data.items()]
