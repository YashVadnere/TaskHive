package com.example.TaskHive.dto;

import com.example.TaskHive.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvitationSendRequestDto
{
    private Role role;
}
