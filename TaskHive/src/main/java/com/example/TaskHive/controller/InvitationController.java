package com.example.TaskHive.controller;

import com.example.TaskHive.dto.InvitationReceiverDto;
import com.example.TaskHive.dto.InvitationReceiverResponseDto;
import com.example.TaskHive.dto.InvitationSendRequestDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.service_interface.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InvitationController
{
    private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService)
    {
        this.invitationService = invitationService;
    }

    @PostMapping("/invitations/projects/{projectId}/senders/{senderId}/receivers/{receiverId}")
    public ResponseEntity<ResponseDto> sendInvitation(
            @RequestBody InvitationSendRequestDto dto,
            @PathVariable("projectId") Long projectId,
            @PathVariable("senderId") Long senderId,
            @PathVariable("receiverId") Long receiverId
    ) {
        return new ResponseEntity<>(invitationService.sendInvitation(dto, projectId, senderId, receiverId), HttpStatus.OK);
    }

    @GetMapping("/invitations/receivers/{receiverId}")
    public ResponseEntity<List<InvitationReceiverDto>> getAllInvitationById(@PathVariable("receiverId") Long receiverId)
    {
        return new ResponseEntity<>(invitationService.getAllInvitationById(receiverId), HttpStatus.OK);
    }

    @PutMapping("/invitations/{invitationId}/users/{receiverId}")
    public ResponseEntity<ResponseDto> invitationResponse(
            @PathVariable("invitationId") Long invitationId,
            @PathVariable("receiverId") Long receiverId,
            @RequestBody InvitationReceiverResponseDto dto
    ) {
        return new ResponseEntity<>(invitationService.invitationResponse(invitationId, receiverId, dto), HttpStatus.OK);
    }

}
