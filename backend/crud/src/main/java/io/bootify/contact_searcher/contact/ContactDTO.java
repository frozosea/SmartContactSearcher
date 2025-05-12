package io.bootify.contact_searcher.contact;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContactDTO {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String jobTitle;

    @Size(max = 1000)
    private String email;

    @Size(max = 1000)
    private String phone;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private Integer tags;

    @NotNull
    private Integer owner;

    private List<Integer> contactTagTags;

}
