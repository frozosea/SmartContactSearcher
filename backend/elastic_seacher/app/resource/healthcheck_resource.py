from fastapi import APIRouter, Depends, HTTPException

from app.util.config import config
from app.util.logger import get_logger

router = APIRouter()


def get_elastic_repository():
    # Import your ElasticRepository implementation (assumed to exist)
    from elasticsearch.client import Elasticsearch
    return Elasticsearch(hosts=[{"host": config.ELASTIC_HOST, "port": config.ELASTICSEARCH_PORT}],
                         # basic_auth=("elastic", config.ELASTICSEARCH_PASSWORD),
                         scheme="http",
                         verify_certs=False)


@router.get(
    "/health",
    tags=["Health Check"],
    summary="Health Check Endpoint",
    description="Checks if the ElasticSearch connection is active."
)
def health_check(elastic_repo=Depends(get_elastic_repository)):
    logger = get_logger()
    try:
        # Assume that ElasticRepository has a 'ping' method that returns True if connected
        if not elastic_repo.ping():
            logger.error("ElasticSearch ping failed.")
            raise HTTPException(status_code=503, detail="ElasticSearch is down")
        return {"status": "healthy"}
    except Exception as e:
        logger.error(f"Health check failed: {str(e)}")
        raise HTTPException(status_code=500, detail="Health check failed")
