package com.example.finder.exception.file;

public class NullFileException extends RuntimeException {
    public NullFileException(String message) {
        super(message);
    }

    public NullFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullFileException(){
        super("A null file was given when expecting file");
    }
}
