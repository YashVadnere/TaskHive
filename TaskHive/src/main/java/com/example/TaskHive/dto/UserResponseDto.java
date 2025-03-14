package com.example.TaskHive.dto;

import com.example.TaskHive.entity.ActivePlan;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto
{
    private Long userId;
    private String fullName;
    private String jobTitle;
    private String email;
    private Long noOfProjects;
    private ActivePlan activePlan;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastLogin;
    private String imageUrl;
}
