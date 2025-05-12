package io.bootify.contact_searcher.query;

import lombok.Getter;
import lombok.Setter;

/**
 * Mirrors the Python service responses like:
 * {
 *   "success": true,
 *   "message": "string"
 * }
 */
@Getter
@Setter
public class BaseResponse {
    private boolean success;
    private String message;
}