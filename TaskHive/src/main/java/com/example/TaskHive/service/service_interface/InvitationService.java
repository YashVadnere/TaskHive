package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.InvitationReceiverDto;
import com.example.TaskHive.dto.InvitationReceiverResponseDto;
import com.example.TaskHive.dto.InvitationSendRequestDto;
import com.example.TaskHive.dto.ResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvitationService
{

    ResponseDto sendInvitation(InvitationSendRequestDto dto, Long projectId, UserDetails userDetails, Long receiverId);

    List<InvitationReceiverDto> getAllInvitationById(UserDetails userDetails);

    ResponseDto invitationResponse(Long invitationId, UserDetails userDetails, InvitationReceiverResponseDto dto);
}
