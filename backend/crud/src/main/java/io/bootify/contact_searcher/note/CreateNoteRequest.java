package io.bootify.contact_searcher.note;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateNoteRequest {

    @NotNull
    private String content;

    @NotNull
    private Integer contact;
}