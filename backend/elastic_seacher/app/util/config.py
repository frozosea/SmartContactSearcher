import os

import dotenv


class Config:
    ELASTIC_HOST = os.getenv("ELASTIC_HOST", "localhost")
    ELASTICSEARCH_HOST = os.getenv("ELASTIC_HOST", "localhost")
    ELASTICSEARCH_PORT = os.getenv("ELASTIC_PORT", "9200")
    ELASTICSEARCH_USER = os.getenv("ELASTICSEARCH_USER", "elastic")
    ELASTICSEARCH_PASSWORD = os.getenv("ELASTICSEARCH_PASSWORD", "aEP+D=mvULHJ6--KihWO")
    ELASTICSEARCH_INDEX_NAME = os.getenv("ELASTICSEARCH_INDEX_NAME", "test_index")
    EMBEDDING_API_URL = os.getenv("EMBEDDING_API_URL", "http://localhost:5000/embed")
    POSTGRES_URL = os.getenv("POSTGRES_URL", "postgresql://user:password@localhost/db")
    ALLOWED_IPS = os.getenv("ALLOWED_IPS", "127.0.0.1, localhost, 0.0.0.0").split(",")  # Comma-separated list of IPs
    RATE_LIMIT = int(os.getenv("RATE_LIMIT", 100000))  # Requests per second
    STORE_LOGS = os.getenv("STORE_LOGS", "FALSE").upper() == "TRUE"
    LOG_LEVEL = os.getenv("LOG_LEVEL", "DEBUG")

dotenv.load_dotenv()
config = Config()
