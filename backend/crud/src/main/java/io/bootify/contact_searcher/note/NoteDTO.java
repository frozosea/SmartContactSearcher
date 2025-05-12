package io.bootify.contact_searcher.note;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoteDTO {

    private Integer id;

    @NotNull
    private String content;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @NotNull
    private Integer contact;

}
