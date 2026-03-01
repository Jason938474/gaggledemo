package com.gaggledemo.controllers.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DocumentUpdateRequestDto {
    @NotBlank(message = "Tile cannot be blank")
    public final String title;

    @NotBlank(message = "Content cannot be blank")
    public final String content;

    @NotNull
    @Min(value=1, message="Key for lastEditedBy must be positive")
    public final Integer lastEditedBy;

    public DocumentUpdateRequestDto(@JsonProperty("title") String title,
                                    @JsonProperty("content") String content,
                                    @JsonProperty("lastEditedBy") Integer lastEditedBy) {
        this.title = title;
        this.content = content;
        this.lastEditedBy = lastEditedBy;
    }
}
