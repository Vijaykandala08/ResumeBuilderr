package com.spring.resumebuilder.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message); // calling parent class constructor and passing the msg to the parent class
    }
}
