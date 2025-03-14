package com.example.TaskHive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto
{
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
