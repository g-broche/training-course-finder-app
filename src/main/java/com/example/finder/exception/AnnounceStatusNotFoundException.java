package com.example.finder.exception;

public class AnnounceStatusNotFoundException extends EntityNotFoundException {
  public AnnounceStatusNotFoundException(String message) {
    super(message);
  }

  public AnnounceStatusNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public AnnounceStatusNotFoundException(){
    super("A reference to an unregistered announce status was made");
  }
}
