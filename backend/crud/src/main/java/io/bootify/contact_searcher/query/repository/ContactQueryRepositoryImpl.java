package io.bootify.contact_searcher.query.repository;

import io.bootify.contact_searcher.query.*;
import io.bootify.contact_searcher.query.requests.*;
import io.bootify.contact_searcher.query.requests.GenerateEmbeddingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class ContactQueryRepositoryImpl implements ContactQueryRepository {

    private final RestTemplate restTemplate;
    private final String pythonBaseUrl;

    public ContactQueryRepositoryImpl(
            RestTemplate restTemplate,
            @Value("${python.service.base-url}") String pythonBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.pythonBaseUrl = pythonBaseUrl;
    }

    @Override
    public boolean checkHealth() {
        String healthUrl = pythonBaseUrl + "/healthcheck/health";
        try {
            restTemplate.getForObject(healthUrl, String.class);
            return true;
        } catch (Exception e) {
            // Could log or rethrow
            throw new IllegalStateException("Python service is unavailable", e);
        }
    }

    @Override
    public List<Float> generateEmbedding(String text) {
        checkHealth();
        String url = pythonBaseUrl + "/embedding/generate";

        GenerateEmbeddingRequest request = new GenerateEmbeddingRequest();
        request.setText(text);

        // Expect a response like: { "embedding": [0.123, 0.456, ...] }
        @SuppressWarnings("unchecked")
        var responseMap = restTemplate.postForObject(url, request, java.util.Map.class);
        if (responseMap != null && responseMap.containsKey("embedding")) {
            return (List<Float>) responseMap.get("embedding");
        }
        return Collections.emptyList();
    }

    @Override
    public BaseResponse createContact(ContactIndexDTO contactIndexDTO) {
        checkHealth();
        String url = pythonBaseUrl + "/contact/create";
        return restTemplate.postForObject(url, contactIndexDTO, BaseResponse.class);
    }

    @Override
    public BaseResponse updateContact(ContactIndexDTO contactIndexDTO) {
        checkHealth();
        String url = pythonBaseUrl + "/contact/update";
        // For a PUT request, we can use exchange or put
        ResponseEntity<BaseResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(contactIndexDTO),
                BaseResponse.class
        );
        return responseEntity.getBody();
    }

    @Override
    public BaseResponse deleteContact(DeleteContactRequest deleteRequest) {
        checkHealth();
        String url = pythonBaseUrl + "/contact/";
        ResponseEntity<BaseResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(deleteRequest),
                BaseResponse.class
        );
        return responseEntity.getBody();
    }

    @Override
    public List<ContactIndexDTO> searchContacts(SearchContactsRequest request) {
        checkHealth();
        String url = pythonBaseUrl + "/contact/search";
        ContactIndexDTO[] resultArray = restTemplate.postForObject(url, request, ContactIndexDTO[].class);
        if (resultArray != null) {
            return Arrays.asList(resultArray);
        }
        return Collections.emptyList();
    }

    @Override
    public List<ContactIndexDTO> searchContactsByTags(SearchByTagsRequest request) {
        checkHealth();
        String url = pythonBaseUrl + "/contact/tags/search";
        ContactIndexDTO[] resultArray = restTemplate.postForObject(url, request, ContactIndexDTO[].class);
        if (resultArray != null) {
            return Arrays.asList(resultArray);
        }
        return Collections.emptyList();
    }
}
