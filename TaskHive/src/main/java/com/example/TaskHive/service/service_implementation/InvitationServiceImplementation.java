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

        String htmlText = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>TaskHive | Verify Your Email</title>\n" +
                "</head>\n" +
                "<body style=\"margin:0; padding:0; background: #f0f9ff; font-family: Arial, sans-serif; color: #333333;\">\n" +
                "\n" +
                "    <table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background: #f0f9ff; padding: 40px 0;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\">\n" +
                "                <table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"max-width: 600px; width: 100%; background-color: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb; box-shadow: 0 10px 20px rgba(0,0,0,0.05);\">\n" +
                "                    \n" +
                "                    <!-- Header -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"background: linear-gradient(to right, #60a5fa, #93c5fd); padding: 24px;\">\n" +
                "                            <h1 style=\"margin: 0; font-size: 28px; color: #ffffff; font-weight: 800;\">TaskHive</h1>\n" +
                "                            <p style=\"margin: 4px 0 0; font-size: 14px; color: #ffffff;\">AI-Enhanced Agile Management</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Body Content -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 40px 30px; background-color: #ffffff;\">\n" +
                "                            <h2 style=\"font-size: 24px; color: #2563eb; margin: 0 0 16px;\">Hello, "+receiver.getFullName()+" \uD83D\uDC4B</h2>\n" +
                "                            <p style=\"color: #4b5563; font-size: 14px; line-height: 1.5; margin: 0 0 24px;\">\n" +
                "                                You have received an invitation from <span style=\"color: #3b82f6; font-weight: 600;\">"+sender.getFullName()+"</span> to collaborate on the project:\n" +
                "                            </p>\n" +
                "\n" +
                "                            <!-- Project Invitation Box -->\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color: #eff6ff; border: 1px solid #93c5fd; border-radius: 16px; text-align: center; margin-bottom: 24px; box-shadow: 0 5px 10px rgba(0,0,0,0.05);\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding: 24px;\">\n" +
                "                                        <p style=\"color: #6b7280; font-size: 12px; text-transform: uppercase; margin: 0 0 8px; letter-spacing: 1px;\">Project Name</p>\n" +
                "                                        <p style=\"color: #2563eb; font-size: 24px; font-weight: 800; margin: 0;\">"+project.getProjectName()+"</p>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "\n" +
                "                            <p style=\"color: #4b5563; font-size: 14px; line-height: 1.5; margin-bottom: 24px; text-align: center;\">\n" +
                "                                Please log in to your TaskHive account to review the details and respond.\n" +
                "                            </p>\n" +
                "\n" +
                "                            <!-- Call-to-Action Button -->\n" +
                "                            <table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0 auto;\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\" bgcolor=\"#3b82f6\" style=\"border-radius: 12px;\">\n" +
                "                                        \n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "\n" +
                "                            <!-- Info Footer -->\n" +
                "                            <p style=\"font-size: 12px; color: #9ca3af; text-align: center; margin-top: 32px; line-height: 1.5;\">\n" +
                "                                Didn’t expect this? Please ignore this email.<br>\n" +
                "                                Need help? <a href=\"#\" style=\"color: #3b82f6; text-decoration: underline;\">Contact Support</a>\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Footer -->\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"background-color: #eff6ff; padding: 16px; font-size: 12px; color: #6b7280;\">\n" +
                "                            Best Regards,<br>\n" +
                "                            <span style=\"color: #2563eb; font-weight: 600; display: inline-block; margin-top: 4px;\">TaskHive Team \uD83D\uDC1D</span>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "\n" +
                "</body>\n" +
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
