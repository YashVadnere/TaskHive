package com.example.TaskHive.exceptions;

public class VerificationCodeExpired extends RuntimeException {
    public VerificationCodeExpired(String message) {
        super(message);
    }
}
