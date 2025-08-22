package com.example.finder.exception.file;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException(String message) {
        super(message);
    }

    public InvalidImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidImageException(){
        super("An invalid image format was given, expecting jpeg, png or webp");
    }
}
