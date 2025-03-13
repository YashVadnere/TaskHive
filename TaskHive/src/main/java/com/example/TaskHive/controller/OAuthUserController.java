package com.example.TaskHive.controller;

import com.example.TaskHive.config.JwtAuthenticationService;
import com.example.TaskHive.entity.Token;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.repository.TokenRepository;
import com.example.TaskHive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class OAuthUserController
{
    private final JwtAuthenticationService jwtAuthenticationService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public OAuthUserController(
            JwtAuthenticationService jwtAuthenticationService,
            UserRepository userRepository,
            TokenRepository tokenRepository
    ) {
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }


    @GetMapping("/user-info")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User oAuth2User)
    {
        Map<String, Object> user = new HashMap<>();
        user.put("email", oAuth2User.getAttribute("email"));
        user.put("firstname", oAuth2User.getAttribute("given_name"));
        user.put("lastname", oAuth2User.getAttribute("family_name"));
        user.put("downloadUrl", oAuth2User.getAttribute("picture"));
        Optional<User> optionalUser = userRepository.findByEmail(oAuth2User.getAttribute("email"));

        if(optionalUser.isEmpty())
        {
            throw new RuntimeException();
        }

        User user1 = optionalUser.get();

        Optional<Token> optionalToken = tokenRepository.findByUser_UserIdAndIsLoggedOutFalse(user1.getUserId());
        String jwtToken;

        if(optionalToken.isPresent())
        {
            jwtToken = optionalToken.get().getToken();
        }
        else
        {
            jwtToken = jwtAuthenticationService.generateToken(user1);
            revokeAllTokens(user1);
            saveToken(jwtToken, user1);
        }

        user.put("token", jwtToken);
        return user;
    }

    private void saveToken(String jwtToken, User user)
    {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setLoggedOut(false);
        token.setUser(user);

        if (user.getTokens() == null)
        {
            user.setTokens(new ArrayList<>());
        }

        user.getTokens().add(token);
        userRepository.save(user);
        tokenRepository.save(token);
    }

    private void revokeAllTokens(User user)
    {
        List<Token> tokenList = tokenRepository.findAllByUser_UserId(user.getUserId());
        if(!tokenList.isEmpty())
        {
            tokenList.forEach(token -> token.setLoggedOut(true));
            tokenRepository.saveAll(tokenList);
        }
    }
}
