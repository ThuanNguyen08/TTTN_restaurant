package com.example.tttn_restaurant.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ErrorResponse {
    private int status;
    private String message;
    private String timestamp;

    @SerializedName("errors")
    private Map<String, String> validationErrors;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}