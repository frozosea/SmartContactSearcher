package io.bootify.contact_searcher.query.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Request body for /embedding/generate in the Python service.
 */
@Getter
@Setter
public class GenerateEmbeddingRequest {
    private String text;
}
