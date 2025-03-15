package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.InvitationReceiverDto;
import com.example.TaskHive.dto.InvitationReceiverResponseDto;
import com.example.TaskHive.dto.InvitationSendRequestDto;
import com.example.TaskHive.dto.ResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvitationService
{

    ResponseDto sendInvitation(InvitationSendRequestDto dto, Long projectId, Long senderId, Long receiverId);

    List<InvitationReceiverDto> getAllInvitationById(Long receiverId);

    ResponseDto invitationResponse(Long invitationId, Long receiverId, InvitationReceiverResponseDto dto);
}
