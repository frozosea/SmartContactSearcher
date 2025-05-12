package io.bootify.contact_searcher.query;

import io.bootify.contact_searcher.query.*;
import io.bootify.contact_searcher.query.requests.*;
import io.bootify.contact_searcher.query.QueryService;
import io.bootify.contact_searcher.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This resource (controller) is exposed at /api/query.
 * It only exposes the embedding generation, text search, and tag search
 * endpoints. The create/update/delete calls are *not* exposed here,
 * to match your requirement.
 */
@RestController
@RequestMapping(value = "/api/query", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryResource {

    private final QueryService queryService;
    private final JwtUtil jwtUtil;

    public QueryResource(QueryService queryService, JwtUtil jwtUtil) {
        this.queryService = queryService;
        this.jwtUtil = jwtUtil;
    }

    private void authUser(String token) {
        // Or use your existing logic to parse and validate the token
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        // Possibly ensure the user ID is valid, etc. as you do elsewhere
    }

    /**
     * 1. Generate Embedding
     */
    @PostMapping("/embedding")
    public ResponseEntity<List<Float>> generateEmbedding(
            @RequestHeader("Authorization") String token,
            @RequestBody GenerateEmbeddingRequest req
    ) {
        authUser(token);
        List<Float> result = queryService.generateEmbedding(req.getText());
        return ResponseEntity.ok(result);
    }

    /**
     * 2. Search Contacts by plain text
     */
    @PostMapping("/search")
    public ResponseEntity<List<ContactIndexDTO>> searchContacts(
            @RequestHeader("Authorization") String token,
            @RequestBody SearchContactsRequest request
    ) {
        authUser(token);
        List<ContactIndexDTO> results = queryService.searchContacts(request);
        return ResponseEntity.ok(results);
    }

    /**
     * 3. Search Contacts by tags
     */
    @PostMapping("/search/tags")
    public ResponseEntity<List<ContactIndexDTO>> searchContactsByTags(
            @RequestHeader("Authorization") String token,
            @RequestBody SearchByTagsRequest request
    ) {
        authUser(token);
        List<ContactIndexDTO> results = queryService.searchContactsByTags(request);
        return ResponseEntity.ok(results);
    }
}
