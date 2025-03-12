package com.example.TaskHive.config;

import com.example.TaskHive.entity.Token;
import com.example.TaskHive.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Transactional
public class CustomLogoutHandler implements LogoutHandler
{
    private final TokenRepository tokenRepository;

    @Autowired
    public CustomLogoutHandler(TokenRepository tokenRepository)
    {
        this.tokenRepository = tokenRepository;
    }


    @Override
    public void logout(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Authentication authentication
    ) {
        String authHeader = request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer "))
        {
            return;
        }

        String jwtToken = authHeader.substring(7);
        Optional<Token> optionalToken = tokenRepository.findByToken(jwtToken);

        if(optionalToken.isPresent())
        {
            Token token = optionalToken.get();
            token.setLoggedOut(true);
            tokenRepository.save(token);
        }
    }
}
