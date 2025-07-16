package com.example.finder.exception;

public class InteractivityStateNotFoundException extends EntityNotFoundException {
  public InteractivityStateNotFoundException(String message) {
    super(message);
  }

  public InteractivityStateNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public InteractivityStateNotFoundException(){
    super("A reference to an unregistered interactivity state was made");
  }
}
