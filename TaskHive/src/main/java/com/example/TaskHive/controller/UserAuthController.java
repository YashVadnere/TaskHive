package com.example.TaskHive.controller;

import com.example.TaskHive.dto.*;
import com.example.TaskHive.service.service_interface.UserAuthServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class UserAuthController
{
    private final UserAuthServiceInterface userAuthServiceInterface;

    @Autowired
    public UserAuthController(UserAuthServiceInterface userAuthServiceInterface)
    {
        this.userAuthServiceInterface = userAuthServiceInterface;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto> signUp(@RequestPart UserSignUpDto dto, @RequestPart MultipartFile file)
    {
        return new ResponseEntity<>(userAuthServiceInterface.signUp(dto, file), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDto> verify(@RequestBody UserVerifyDto dto)
    {
        return new ResponseEntity<>(userAuthServiceInterface.verify(dto),HttpStatus.OK);
    }

    @PostMapping("/re-verify/{email}")
    public ResponseEntity<ResponseDto> reverify(@PathVariable("email") String email)
    {
        return new ResponseEntity<>(userAuthServiceInterface.reverify(email),HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseTokenDto> signIn(@RequestBody UserSignInDto dto)
    {
        return new ResponseEntity<>(userAuthServiceInterface.signIn(dto),HttpStatus.OK);
    }

}
