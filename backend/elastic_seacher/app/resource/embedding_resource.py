from fastapi import APIRouter, HTTPException, Depends
from app.models import GenerateEmbeddingRequest, IndexEmbeddingRequest, SearchEmbeddingRequest
from app.service.embedding_service import EmbeddingService
from app.repository.elastic_repository import ElasticRepository
from app.repository.embedding_repository import OfflineEmbeddingRepository
from app.util.logger import get_logger

router = APIRouter()


def get_embedding_service():
    # Instantiate your ElasticRepository (and/or other dependencies) as required
    return EmbeddingService(OfflineEmbeddingRepository(ElasticRepository()))


@router.post(
    "/generate",
    response_model=dict,
    summary="Generate Embedding",
    description="Generates an embedding for the provided text using the external API."
)
def generate_embedding(request: GenerateEmbeddingRequest, service: EmbeddingService = Depends(get_embedding_service)):
    try:
        embedding = service.generate_embedding(request.text)
        return {"embedding": embedding}
    except Exception as e:
        get_logger().error(f"Error in generate_embedding endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post(
    "/index",
    response_model=dict,
    summary="Index a new embedding",
    description="Generates an embedding for the contact and indexes it into ElasticSearch."
)
def index_embedding(request: IndexEmbeddingRequest, service: EmbeddingService = Depends(get_embedding_service)):
    try:
        # Combine fields as in your service logic
        combined_text = " ".join([request.name, request.jobTitle, " ".join(request.tags), request.notesCombined])
        embedding = service.generate_embedding(combined_text)
        embedding_data = {
            "name": request.name,
            "jobTitle": request.jobTitle,
            "tags": request.tags,
            "notesCombined": request.notesCombined,
            "embedding": embedding,
        }
        service.index_embedding(document_id=request.contactId, user_id=request.userId, embedding_data=embedding_data)
        return {"detail": "Embedding indexed successfully"}
    except Exception as e:
        get_logger().error(f"Error in index_embedding endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post(
    "/search",
    response_model=dict,
    summary="Search by Embedding",
    description="Performs a semantic search using an embedding query vector."
)
def search_by_embedding(request: SearchEmbeddingRequest, service: EmbeddingService = Depends(get_embedding_service)):
    try:
        results = service.search_by_embedding(
            query_vector=request.query_vector,
            user_id=request.user_id,
            k=request.k,
            num_candidates=request.num_candidates,
        )
        return {"results": results}
    except Exception as e:
        get_logger().error(f"Error in search_by_embedding endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
