package com.example.finder.exception;

public class UserStatusNotFoundException extends EntityNotFoundException {
  public UserStatusNotFoundException(String message) {
    super(message);
  }

  public UserStatusNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserStatusNotFoundException(){
    super("A reference to an unregistered user status was made");
  }
}
