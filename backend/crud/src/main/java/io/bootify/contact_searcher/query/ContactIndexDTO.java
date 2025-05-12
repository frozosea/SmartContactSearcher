package io.bootify.contact_searcher.query;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the JSON structure used by the Python service for
 * creating/updating/searching contacts in Elasticsearch.
 */
@Getter
@Setter
public class ContactIndexDTO {

    private String contactId;
    private String userId;
    private String name;
    private String jobTitle;
    private List<String> tags = new ArrayList<>();
    private String notesCombined;

    // In search results, the Python service also returns an embedding array
    private List<Float> embedding;
}
