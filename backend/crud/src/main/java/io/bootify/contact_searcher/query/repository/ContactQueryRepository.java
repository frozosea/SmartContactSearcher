package io.bootify.contact_searcher.query.repository;

import io.bootify.contact_searcher.query.*;
import io.bootify.contact_searcher.query.requests.DeleteContactRequest;
import io.bootify.contact_searcher.query.requests.SearchByTagsRequest;
import io.bootify.contact_searcher.query.requests.SearchContactsRequest;

import java.util.List;

public interface ContactQueryRepository {

    /**
     * Check if Python service is healthy and available.
     * Throws an exception if not healthy or returns false if you prefer.
     */
    boolean checkHealth();

    /**
     * Generate an embedding for a given text using Python's /embedding/generate.
     */
    List<Float> generateEmbedding(String text);

    /**
     * Create a contact in the Python (Elasticsearch) service.
     */
    BaseResponse createContact(ContactIndexDTO contactIndexDTO);

    /**
     * Update an existing contact in the Python (Elasticsearch) service.
     */
    BaseResponse updateContact(ContactIndexDTO contactIndexDTO);

    /**
     * Delete a contact in the Python (Elasticsearch) service.
     */
    BaseResponse deleteContact(DeleteContactRequest deleteRequest);

    /**
     * Search contacts via plain text in the Python service.
     */
    List<ContactIndexDTO> searchContacts(SearchContactsRequest request);

    /**
     * Search contacts by tags in the Python service.
     */
    List<ContactIndexDTO> searchContactsByTags(SearchByTagsRequest request);
}
