package io.bootify.contact_searcher.query.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /contact/search in the Python service.
 */
@Getter
@Setter
public class SearchContactsRequest {
    private String user_id;
    private String query_text;
    private Integer k = 10;
    private Integer num_candidates = 100;
}
