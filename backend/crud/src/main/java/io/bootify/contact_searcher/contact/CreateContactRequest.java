package io.bootify.contact_searcher.contact;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateContactRequest {

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String jobTitle;

    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String phone;

    private int ownerId;

    private List<Integer> contactTags;

}

