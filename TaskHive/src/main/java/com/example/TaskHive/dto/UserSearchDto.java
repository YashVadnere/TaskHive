package com.example.TaskHive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDto
{
    private Long userId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String imageUrl;
}
