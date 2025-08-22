package com.example.finder.exception.entity;

public class CategoryNotFoundException extends EntityNotFoundException {
  public CategoryNotFoundException(String message) {
    super(message);
  }

  public CategoryNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public CategoryNotFoundException(){
    super("A reference to an unregistered category was made");
  }
}
