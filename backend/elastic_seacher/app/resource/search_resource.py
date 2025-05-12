from typing import List

from fastapi import APIRouter, HTTPException, Depends

from app import models
from app.models import SearchContactsRequest, SearchByTagsRequest, Contact, DeleteContactRequest, CreateContactRequest
from app.service.search_service import SearchService
from app.repository.elastic_repository import ElasticRepository
from app.repository.embedding_repository import OfflineEmbeddingRepository
from app.util.logger import get_logger

router = APIRouter()


def get_search_service():
    # Instantiate your repository implementations.
    elastic_repo = ElasticRepository()
    embedding_repo = OfflineEmbeddingRepository(elastic_repo)
    return SearchService(elastic_repo, embedding_repo)


@router.post(
    "/search",
    response_model=List[Contact],
    summary="Semantic search for contacts",
    description="Perform a semantic search using a query text to find matching contacts."
)
def search_contacts(request: SearchContactsRequest, service: SearchService = Depends(get_search_service)):
    try:
        results = service.search_contacts(
            user_id=request.user_id,
            query_text=request.query_text,
            k=request.k,
            num_candidates=request.num_candidates,
        )
        return results
    except Exception as e:
        get_logger().error(f"Error in search_contacts endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post(
    "/tags/search",
    response_model=List[Contact],
    summary="Search contacts by tags",
    description="Perform a search for contacts based on a list of tags."
)
def search_by_tags(request: SearchByTagsRequest, service: SearchService = Depends(get_search_service)):
    try:
        results = service.search_by_tags(
            user_id=request.user_id,
            tags=request.tags,
        )
        return results
    except Exception as e:
        get_logger().error(f"Error in search_by_tags endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post(
    "/create",
    response_model=models.BaseResponse,
    summary="Create a new contact",
    description="Saves a new contact by generating an embedding and indexing it into ElasticSearch."
)
def create_contact(contact: CreateContactRequest, service: SearchService = Depends(get_search_service)):
    try:
        service.save_new_contact(contact.dict())
        return {"success": True, "message": "Contact created successfully"}
    except Exception as e:
        get_logger().error(f"Error in create_contact endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@router.put(
    "/",
    response_model=models.BaseResponse,
    summary="Update an existing contact",
    description="Updates contact information by regenerating its embedding and updating it in ElasticSearch."
)
def update_contact(contact: Contact, service: SearchService = Depends(get_search_service)):
    try:
        service.update_contact_info(contact.dict())
        return {"success": True, "message": "Contact updated successfully"}
    except Exception as e:
        get_logger().error(f"Error in update_contact endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@router.delete(
    "/",
    response_model=models.BaseResponse,
    summary="Delete an existing contact",
    description="Delete contact information by contact_id and its embedding and updating it in ElasticSearch."
)
def delete_contact(contact: DeleteContactRequest, service: SearchService = Depends(get_search_service)):
    try:
        service.delete_contact(contact.contactId)
        return {"success": True, "message": "Contact deleted successfully"}
    except Exception as e:
        get_logger().error(f"Error in update_contact endpoint: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
