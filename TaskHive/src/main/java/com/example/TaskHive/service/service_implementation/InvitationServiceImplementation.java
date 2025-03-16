package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.InvitationReceiverDto;
import com.example.TaskHive.dto.InvitationReceiverResponseDto;
import com.example.TaskHive.dto.InvitationSendRequestDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.*;
import com.example.TaskHive.exceptions.BadRequestException;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.InvitationRepository;
import com.example.TaskHive.repository.ProjectMemberRepository;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.EmailService;
import com.example.TaskHive.service.service_interface.InvitationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationServiceImplementation implements InvitationService
{
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EmailService emailService;
    private final ProjectMemberRepository projectMemberRepository;

    @Autowired
    public InvitationServiceImplementation(
            InvitationRepository invitationRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            EmailService emailService,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.emailService = emailService;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    @Transactional
    public ResponseDto sendInvitation(InvitationSendRequestDto dto, Long projectId, Long senderId, Long receiverId)
    {
        Invitation invitation = mapInvitationSendRequestDtoToInvitationEntity(dto);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFound("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFound("Receiver not found"));

        Project project = projectRepository.findById(projectId)
                        .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(project.getUser() == null || !project.getUser().getUserId().equals(sender.getUserId()))
        {
            throw new Mismatch("Sender does not have the following project created");
        }

        invitation.setInvitationStatus(InvitationStatus.PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setRespondedAt(null);
        invitation.setInviter(sender);
        invitation.setInvitee(receiver);
        invitation.setProject(project);
        invitationRepository.save(invitation);

        project.getInvitations().add(invitation);
        sender.getSentInvitations().add(invitation);
        receiver.getReceivedInvitations().add(invitation);

        projectRepository.save(project);
        userRepository.save(sender);
        userRepository.save(receiver);

        sendInvitationNotificationEmail(sender, receiver, project);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Invitation sent successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public List<InvitationReceiverDto> getAllInvitationById(Long receiverId)
    {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFound("Receiver not found"));


        List<Invitation> invitations = receiver.getReceivedInvitations();

        return invitations.stream()
                .map(this::mapReceiverEntityToInvitationReceiverDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseDto invitationResponse(Long invitationId, Long receiverId, InvitationReceiverResponseDto dto)
    {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFound("Invitation not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFound("Receiver not found"));

        if (dto.getInvitationStatus() == null)
        {
            throw new BadRequestException("Invitation status cannot be null");
        }

        invitation.setInvitationStatus(dto.getInvitationStatus());
        invitation.setRespondedAt(LocalDateTime.now());

        invitationRepository.save(invitation);

        ResponseDto responseDto = new ResponseDto();

        if(dto.getInvitationStatus() == InvitationStatus.ACCEPTED)
        {
            ProjectMember projectMember = new ProjectMember();
            projectMember.setRole(invitation.getRole());
            projectMember.setJoinedAt(LocalDateTime.now());
            projectMember.setUser(receiver);
            projectMember.setProject(invitation.getProject());
            projectMemberRepository.save(projectMember);
            responseDto.setMessage("Invitation Accepted");
            return responseDto;
        }
        else {
            responseDto.setMessage("Invitation Declined");
            return responseDto;
        }
    }

    private void sendInvitationNotificationEmail(User sender, User receiver, Project project)
    {
        String to = receiver.getEmail();
        String subject = "You've Received a Project Invitation!";

        String htmlText = "<html>\n" +
                "  <body>\n" +
                "    <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "      <tr>\n" +
                "        <td align=\"center\">\n" +
                "          <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"10\" style=\"border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding: 20px; font-family: Arial, sans-serif;\">\n" +
                "                <h2 style=\"color: #0056b3;\">Project Invitation</h2>\n" +
                "                <p>Hello <strong>" + receiver.getFullName() + "</strong>,</p>\n" +
                "                <p>You have received an invitation from <strong>" + sender.getFullName() + "</strong> to collaborate on the project:</p>\n" +
                "                <h3 style=\"color: #333;\">" + project.getProjectName() + "</h3>\n" +
                "                <p>Please log in to your TaskHive account to review the details and respond.</p>\n" +
                "                <p>Best Regards, <br><strong>TaskHive Team</strong></p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>";


        emailService.sendEmailToUser(to,subject,htmlText);
    }

    private InvitationReceiverDto mapReceiverEntityToInvitationReceiverDto(Invitation invitation)
    {
        InvitationReceiverDto dto = new InvitationReceiverDto();
        dto.setInvitationId(invitation.getInvitationId());
        dto.setInviterId(invitation.getInviter().getUserId());
        dto.setProjectId(invitation.getProject().getProjectId());
        dto.setProjectName(invitation.getProject().getProjectName());
        dto.setInviterName(invitation.getInviter().getFullName());
        dto.setInviteeId(invitation.getInvitee().getUserId());
        dto.setInviteeName(invitation.getInvitee().getFullName());
        dto.setCreatedAt(invitation.getCreatedAt());
        dto.setInvitationStatus(invitation.getInvitationStatus());
        return dto;
    }

    private Invitation mapInvitationSendRequestDtoToInvitationEntity(InvitationSendRequestDto dto)
    {
        Invitation invitation = new Invitation();
        invitation.setRole(dto.getRole());
        return invitation;
    }
}
