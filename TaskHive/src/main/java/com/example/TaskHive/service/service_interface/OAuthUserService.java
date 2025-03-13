package com.example.TaskHive.service.service_interface;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface OAuthUserService 
{

    Map<String, Object> user(OAuth2User oAuth2User);
}
