package com.example.TaskHive.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptions
{
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFound e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VerificationCodeExpired.class)
    public ResponseEntity<?> handleVerificationCodeExpired(VerificationCodeExpired e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectVerificationCode.class)
    public ResponseEntity<?> handleIncorrectVerificationCode(IncorrectVerificationCode e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentials e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyRegistered.class)
    public ResponseEntity<?> handleUserAlreadyRegistered(UserAlreadyRegistered e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
