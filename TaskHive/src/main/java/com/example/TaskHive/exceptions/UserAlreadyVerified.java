package com.example.TaskHive.exceptions;

public class UserAlreadyVerified extends RuntimeException {
    public UserAlreadyVerified(String message) {
        super(message);
    }
}
