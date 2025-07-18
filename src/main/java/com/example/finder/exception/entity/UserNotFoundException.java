package com.example.finder.exception.entity;

public class UserNotFoundException extends EntityNotFoundException {
  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserNotFoundException(){
    super("No such user exists");
  }
}
