package com.example.finder.exception;

public class AnnounceTypeNotFoundException extends EntityNotFoundException {
  public AnnounceTypeNotFoundException(String message) {
    super(message);
  }

  public AnnounceTypeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public AnnounceTypeNotFoundException(){
    super("A reference to an unregistered announce type was made");
  }
}
