package com.example.TaskHive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDto
{
    private Long userId;
    private String fullName;
    private String email;
}
