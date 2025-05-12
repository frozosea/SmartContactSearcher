from pydantic import BaseModel, Field
from typing import List, Optional


class Contact(BaseModel):
    contactId: str = Field(..., description="Unique identifier for the contact")
    userId: str = Field(..., description="User identifier")
    name: str = Field(..., description="Contact's name")
    jobTitle: str = Field(..., description="Contact's job title")
    tags: List[str] = Field(..., description="List of tags associated with the contact")
    notesCombined: str = Field(..., description="Combined notes for the contact")
    embedding: Optional[List[float]] = Field(None, description="Embedding vector for the contact")


class CreateContactRequest(BaseModel):
    contactId: str = Field(..., description="Unique identifier for the contact")
    userId: str = Field(..., description="User identifier")
    name: str = Field(..., description="Contact's name")
    jobTitle: str = Field(..., description="Contact's job title")
    tags: List[str] = Field(..., description="List of tags associated with the contact")
    notesCombined: str = Field(..., description="Combined notes for the contact")

class SearchContactsRequest(BaseModel):
    user_id: str = Field(..., description="User identifier")
    query_text: str = Field(..., description="Search query text")
    k: Optional[int] = Field(10, description="Number of top results to return")
    num_candidates: Optional[int] = Field(100, description="Number of candidate results for kNN search")

class DeleteContactRequest(BaseModel):
    user_id: str = Field(..., description="User identifier")
    contactId: str = Field(..., description="Unique identifier for the contact")


class SearchByTagsRequest(BaseModel):
    user_id: str = Field(..., description="User identifier")
    tags: List[str] = Field(..., description="List of tags to search for")


class GenerateEmbeddingRequest(BaseModel):
    text: str = Field(..., description="Input text to generate an embedding for")


class IndexEmbeddingRequest(BaseModel):
    contactId: str = Field(..., description="Unique identifier for the contact")
    userId: str = Field(..., description="User identifier")
    name: str = Field(..., description="Contact's name")
    jobTitle: str = Field(..., description="Contact's job title")
    tags: List[str] = Field(..., description="List of tags associated with the contact")
    notesCombined: str = Field(..., description="Combined notes for the contact")


class SearchEmbeddingRequest(BaseModel):
    query_vector: List[float] = Field(..., description="Embedding vector for the search query")
    user_id: str = Field(..., description="User identifier")
    k: Optional[int] = Field(10, description="Number of top results to return")
    num_candidates: Optional[int] = Field(100, description="Number of candidate results for kNN search")

class BaseResponse(BaseModel):
    success: bool
    message: str