package io.bootify.contact_searcher.query.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Request body for DELETE /contact/ in the Python service.
 */
@Getter
@Setter
public class DeleteContactRequest {
    private String user_id;
    private String contactId;
}
