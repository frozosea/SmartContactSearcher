package io.bootify.contact_searcher.query.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request body for POST /contact/tags/search in the Python service.
 */
@Getter
@Setter
public class SearchByTagsRequest {
    private String user_id;
    private List<String> tags;
}
