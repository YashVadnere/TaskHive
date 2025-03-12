package com.example.TaskHive.repository;

import com.example.TaskHive.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long>
{
    List<Token> findAllByUser_UserId(Long userId);
    Optional<Token> findByToken(String jwtToken);
}
