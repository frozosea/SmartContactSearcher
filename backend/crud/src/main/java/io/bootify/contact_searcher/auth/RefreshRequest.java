package io.bootify.contact_searcher.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}