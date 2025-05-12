from elasticsearch import Elasticsearch
import numpy as np

# Connect to Elasticsearch
from app.util.config import config

elastic = Elasticsearch(
    hosts=[{"host": "localhost", "port": 9200}],
    # basic_auth=("elastic", config.ELASTICSEARCH_PASSWORD),
    scheme="http",
    verify_certs=False
)

# Example query embedding (should be of the same size as the indexed embeddings)
query_embedding = np.random.rand(384).tolist()  # Example query vector

# Perform k-NN search using cosine similarity
response = elastic.search(
    index="test_index",
    body={
        "size": 5,
        "query": {
            "knn": {
                "embedding": {
                    "vector": query_embedding,
                    "k": 5
                }
            }
        }
    }
)

# Display the results
print(response)
