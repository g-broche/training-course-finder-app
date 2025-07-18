package com.example.finder.exception.entity;

public class RoleNotFoundException extends EntityNotFoundException {
  public RoleNotFoundException(String message) {
    super(message);
  }

  public RoleNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RoleNotFoundException(){
    super("A reference to an unregistered role was made");
  }
}
