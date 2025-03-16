package com.example.TaskHive.controller;

import com.example.TaskHive.dto.*;
import com.example.TaskHive.service.service_interface.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class UserAuthController
{
    private final UserAuthService userAuthService;

    @Autowired
    public UserAuthController(UserAuthService userAuthService)
    {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto> signUp(
            @RequestPart UserSignUpDto dto,
            @RequestPart(required = false) MultipartFile file
    ) {
        return new ResponseEntity<>(userAuthService.signUp(dto, file), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDto> verify(@RequestBody UserVerifyDto dto)
    {
        return new ResponseEntity<>(userAuthService.verify(dto),HttpStatus.OK);
    }

    @PostMapping("/re-verify")
    public ResponseEntity<ResponseDto> reVerify(@AuthenticationPrincipal UserDetails userDetails)
    {
        return new ResponseEntity<>(userAuthService.reverify(userDetails),HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseTokenDto> signIn(@RequestBody UserSignInDto dto)
    {
        return new ResponseEntity<>(userAuthService.signIn(dto),HttpStatus.OK);
    }



}
