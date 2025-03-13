package com.example.TaskHive.exceptions;

public class ProjectLimitExceeded extends RuntimeException {
    public ProjectLimitExceeded(String message) {
        super(message);
    }
}
