package com.example.TaskHive.exceptions;

public class IncorrectVerificationCode extends RuntimeException {
    public IncorrectVerificationCode(String message) {
        super(message);
    }
}
