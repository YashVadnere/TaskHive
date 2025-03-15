package com.example.TaskHive.dto;

import com.example.TaskHive.entity.InvitationStatus;
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
public class InvitationReceiverDto
{
    private Long invitationId;
    private Long inviterId;
    private Long projectId;
    private String projectName;
    private String inviterName;
    private Long inviteeId;
    private String inviteeName;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private InvitationStatus invitationStatus;

}
