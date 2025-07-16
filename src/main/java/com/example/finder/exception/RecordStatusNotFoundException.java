package com.example.finder.exception;

public class RecordStatusNotFoundException extends EntityNotFoundException {
  public RecordStatusNotFoundException(String message) {
    super(message);
  }

  public RecordStatusNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecordStatusNotFoundException(){
    super("A reference to an unregistered record status was made");
  }
}
