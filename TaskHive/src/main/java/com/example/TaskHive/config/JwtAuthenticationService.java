package com.example.TaskHive.config;

import com.example.TaskHive.entity.Token;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtAuthenticationService
{
    @Value("${spring.security.secret-key}")
    private String secretKey;
    @Value("${spring.security.expiration}")
    private Long expiration;

    private final TokenRepository tokenRepository;

    @Autowired
    public JwtAuthenticationService(TokenRepository tokenRepository)
    {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(User user)
    {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .issuer("TaskHive")
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .and()
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey()
    {
        byte[] decode = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUsername(String jwtToken)
    {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    private <T>T extractClaims(String jwtToken, Function<Claims,T> claimsResolver)
    {
        Claims claims = extractClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String jwtToken)
    {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails)
    {
        String username = extractUsername(jwtToken);
        boolean isLoggedOut = true;
        Optional<Token> optionalToken = tokenRepository.findByToken(jwtToken);

        if(optionalToken.isPresent())
        {
            isLoggedOut = optionalToken.get().isLoggedOut();
        }

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken) && !isLoggedOut);
    }

    private boolean isTokenExpired(String jwtToken)
    {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken)
    {
        return extractClaims(jwtToken, Claims::getExpiration);
    }

}
