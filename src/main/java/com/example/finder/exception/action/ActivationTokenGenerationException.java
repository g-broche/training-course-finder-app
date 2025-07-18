package com.example.finder.exception.action;

public class ActivationTokenGenerationException extends RuntimeException {
    public ActivationTokenGenerationException(String message) {
        super(message);
    }

    public ActivationTokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivationTokenGenerationException(){
        super("An error occurred that prevented the activation token from being generated");
    }
}
