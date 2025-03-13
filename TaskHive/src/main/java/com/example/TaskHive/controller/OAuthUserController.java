package com.example.TaskHive.controller;

import com.example.TaskHive.config.JwtAuthenticationService;
import com.example.TaskHive.entity.Token;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.repository.TokenRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.OAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class OAuthUserController
{

    private final OAuthUserService oAuthUserService;

    @Autowired
    public OAuthUserController(OAuthUserService oAuthUserService) {
        this.oAuthUserService = oAuthUserService;
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> user(@AuthenticationPrincipal OAuth2User oAuth2User)
    {
        return new ResponseEntity<>(oAuthUserService.user(oAuth2User), HttpStatus.OK);
    }


}
