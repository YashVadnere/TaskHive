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

    @ExceptionHandler(UserAlreadyVerified.class)
    public ResponseEntity<?> handleUserAlreadyVerified(UserAlreadyVerified e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectLimitExceeded.class)
    public ResponseEntity<?> handleProjectLimitExceeded(ProjectLimitExceeded e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Mismatch.class)
    public ResponseEntity<?> handleMismatch(Mismatch e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<?> handleFileStorageException(FileStorageException e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<?> handlePaymentProcessingException(PaymentProcessingException e)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCurrentTime(LocalDateTime.now());
        exceptionResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
