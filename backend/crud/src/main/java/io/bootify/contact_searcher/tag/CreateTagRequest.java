package io.bootify.contact_searcher.tag;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTagRequest {
    @NotNull
    @Size(max = 50)
    private String name;
}
