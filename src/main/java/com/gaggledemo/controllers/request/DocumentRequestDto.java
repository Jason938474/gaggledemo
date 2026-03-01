package com.gaggledemo.controllers.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DocumentRequestDto {
    @NotBlank(message = "Tile cannot be blank")
    public final String title;

    @NotBlank(message = "Content cannot be blank")
    public final String content;

    @NotNull
    @Min(value=0, message="Key for createdBy must be positive")
    public final Integer createdBy;

    @NotNull
    @Min(value=0, message="Key for lastEditedBy must be positive")
    public final Integer lastEditedBy;


    public DocumentRequestDto(@JsonProperty("title") String title,
                             @JsonProperty("content") String content,
                              @JsonProperty("createdBy") Integer createdBy,
                             @JsonProperty("lastEditedBy") Integer lastEditedBy) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.lastEditedBy = lastEditedBy;
    }
}
