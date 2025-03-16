package com.example.TaskHive.controller;

import com.example.TaskHive.dto.InvitationReceiverDto;
import com.example.TaskHive.dto.InvitationReceiverResponseDto;
import com.example.TaskHive.dto.InvitationSendRequestDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.service_interface.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/projects/{projectId}/invitations/{receiverId}")
    public ResponseEntity<ResponseDto> sendInvitation(
            @RequestBody InvitationSendRequestDto dto,
            @PathVariable("projectId") Long projectId,
            @PathVariable("receiverId") Long receiverId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(invitationService.sendInvitation(dto, projectId, userDetails, receiverId), HttpStatus.OK);
    }

    @GetMapping("/invitations")
    public ResponseEntity<List<InvitationReceiverDto>> getAllInvitationById(@AuthenticationPrincipal UserDetails userDetails)
    {
        return new ResponseEntity<>(invitationService.getAllInvitationById(userDetails), HttpStatus.OK);
    }

    @PutMapping("/invitations/{invitationId}")
    public ResponseEntity<ResponseDto> invitationResponse(
            @PathVariable("invitationId") Long invitationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody InvitationReceiverResponseDto dto
    ) {
        return new ResponseEntity<>(invitationService.invitationResponse(invitationId, userDetails, dto), HttpStatus.OK);
    }

}
