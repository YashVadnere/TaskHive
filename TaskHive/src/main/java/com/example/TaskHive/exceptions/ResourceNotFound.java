package com.example.TaskHive.exceptions;

public class ResourceNotFound extends RuntimeException
{
    public ResourceNotFound(String message) {
        super(message);
    }
}
