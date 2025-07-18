package com.example.finder.dto.output;

public class ErrorDto {
    private String ErrorName;
    private String ErrorMessage;

    public ErrorDto(String errorName, String errorMessage) {
        ErrorName = errorName;
        ErrorMessage = errorMessage;
    }

    public String getErrorName() {
        return ErrorName;
    }

    public void setErrorName(String errorName) {
        ErrorName = errorName;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
