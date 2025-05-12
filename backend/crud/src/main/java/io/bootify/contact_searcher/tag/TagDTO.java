package io.bootify.contact_searcher.tag;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TagDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String name;

}
