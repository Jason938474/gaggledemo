package com.gaggledemo.controllers.response;

import java.util.Map;

public class ErrorResponse {

    public final String message;
    public final Map<String, String> errors;

    public ErrorResponse(String message, Map<String, String> errors) {
        this.message =message;
        this.errors =errors;
    }
}
