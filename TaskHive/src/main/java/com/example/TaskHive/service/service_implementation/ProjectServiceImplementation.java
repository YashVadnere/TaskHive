package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.*;
import com.example.TaskHive.entity.*;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ProjectLimitExceeded;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProductBacklogRepository;
import com.example.TaskHive.repository.ProjectMemberRepository;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.ProjectService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImplementation implements ProjectService
{
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProductBacklogRepository productBacklogRepository;

    @Autowired
    public ProjectServiceImplementation(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            ProductBacklogRepository productBacklogRepository
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.productBacklogRepository = productBacklogRepository;
    }

    @Override
    @Transactional
    public ResponseDto createProject(UserDetails userDetails, ProjectPostDto dto)
    {
        User user =userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()->new ResourceNotFound("User not found"));

        if(user.getNoOfProjects() >= user.getProjectLimit())
        {
            throw new ProjectLimitExceeded("Project limit exceeded");
        }

        user.setNoOfProjects(user.getNoOfProjects()+1);
        Project project = mapProjectPostDtoToEntity(dto);
        project.setUser(user);
        user.getProjects().add(project);

        ProjectMember projectMember = new ProjectMember();

        projectMember.setRole(Role.SCRUM_MASTER);
        projectMember.setJoinedAt(LocalDateTime.now());

        projectMember.setUser(user);
        projectMember.setProject(project);

        project.getProjectMembers().add(projectMember);
        user.getProjectMembers().add(projectMember);

        ProductBacklog productBacklog = new ProductBacklog();
        productBacklog.setProductBacklogStatus(ProductBacklogStatus.EMPTY);
        productBacklog.setProject(project);

        projectRepository.save(project);
        userRepository.save(user);
        projectMemberRepository.save(projectMember);
        productBacklogRepository.save(productBacklog);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Project created");
        return responseDto;
    }

    @Override
    @Transactional
    public List<ProjectResponseDto> getAllProjects(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        return user.getProjectMembers()
                .stream()
                .map(ProjectMember::getProject)
                .map(project -> mapProjectEntityToDto(project, user))
                .distinct()
                .toList();

    }

    @Override
    @Transactional
    public ProjectResponseDto getProjectById(Long userId, Long projectId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        boolean isMember = project.getProjectMembers()
                .stream()
                .anyMatch(member -> member.getUser().getUserId().equals(userId));

        if (!isMember) {
            throw new ResourceNotFound("User does not have access to this project");
        }

        return mapProjectEntityToDto(project, user);
    }

    @Override
    @Transactional
    public ResponseDto deleteProjectById(UserDetails userDetails, Long projectId)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new ResourceNotFound("Project not found for this user");
        }


        user.getProjects().remove(project);

        userRepository.save(user);
        projectRepository.deleteById(projectId);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Project deleted successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public ResponseDto updateById(UserDetails userDetails, Long projectId, ProjectUpdateDto dto)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new ResourceNotFound("Project not found for this user");
        }
        if(dto.getProjectName()!=null && !dto.getProjectName().isEmpty())
        {
            project.setProjectName(dto.getProjectName());
        }
        if(dto.getProjectDescription()!=null && !dto.getProjectDescription().isEmpty())
        {
            project.setProjectDescription(dto.getProjectDescription());
        }
        if(dto.getProjectType()!=null && !dto.getProjectType().isEmpty())
        {
            project.setProjectType(dto.getProjectType());
        }
        if(dto.getStartDate()!=null)
        {
            project.setStartDate(dto.getStartDate());
        }
        if(dto.getPriority()!=null && !dto.getPriority().isEmpty())
        {
            project.setPriority(dto.getPriority());
        }
        if(dto.getVisibility()!=null && !dto.getVisibility().isEmpty())
        {
            project.setVisibility(dto.getVisibility());
        }

        projectRepository.save(project);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Project updated successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public List<ProjectResponseDto> search(Long userId, String projectName)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        return user.getProjectMembers().stream()
                .map(ProjectMember::getProject)
                .filter(project -> project.getProjectName().toLowerCase().contains(projectName.toLowerCase()))
                .map(project -> mapProjectEntityToDto(project, user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseDto leaveProjectById(UserDetails userDetails, Long projectId)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        ProjectMember projectMember = project.getProjectMembers()
                .stream()
                .filter(projectMember1 -> projectMember1.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("User is not member of this project"));

        if (projectMember.getRole() == Role.SCRUM_MASTER)
        {
            throw new IllegalStateException("SCRUM_MASTER cannot leave");
        }

        project.getProjectMembers().remove(projectMember);
        user.getProjectMembers().remove(projectMember);

        projectMemberRepository.delete(projectMember);
        projectRepository.save(project);
        userRepository.save(user);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Successfully left the project");
        return responseDto;
    }

    @Override
    @Transactional
    public ResponseDto removeTeamMember(Long projectId, Long memberId, UserDetails userDetails)
    {
        User requestingUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        System.out.println("User : "+userDetails.getUsername());
        System.out.println("Password : "+userDetails.getPassword());

        ProjectMember requestingProjectMember = project.getProjectMembers().stream()
                .filter(member ->  member.getUser().getUserId().equals(requestingUser.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("User is not member of the project"));

        if(!(requestingProjectMember.getRole() == Role.SCRUM_MASTER))
        {
            throw new ResourceNotFound("Only the Scrum Master can remove members from the project");
        }

        ProjectMember memberToRemove = project.getProjectMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("Member not found in this project"));

        User userToBeRemoved = memberToRemove.getUser();

        project.getProjectMembers().remove(memberToRemove);
        userToBeRemoved.getProjectMembers().remove(memberToRemove);

        projectRepository.save(project);
        userRepository.save(userToBeRemoved);

        projectMemberRepository.delete(memberToRemove);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("User deleted successfully");

        return responseDto;
    }

    @Override
    public List<ProjectMemberDto> getAllProjectMembers(Long projectId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        boolean isMember = project.getProjectMembers()
                .stream()
                .anyMatch(member -> member.getUser().getUserId().equals(user.getUserId()));

        if(!isMember)
        {
            throw new Mismatch("User is not a project member");
        }

        return project.getProjectMembers()
                .stream()
                .map(member -> new ProjectMemberDto(
                        member.getUser().getUserId(),
                        member.getUser().getFullName(),
                        member.getUser().getEmail(),
                        member.getRole()
                ))
                .toList();
    }

    private Project mapProjectPostDtoToEntity(ProjectPostDto dto)
    {
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setProjectDescription(dto.getProjectDescription());
        project.setProjectType(dto.getProjectType());
        project.setPriority(dto.getPriority());
        project.setVisibility(dto.getVisibility());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(null);
        project.setProjectStatus(ProjectStatus.ACTIVE);
        project.setCreatedAt(LocalDateTime.now());
        return project;
    }


    private ProjectResponseDto mapProjectEntityToDto(Project project, User user)
    {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setProjectId(project.getProjectId());
        dto.setProjectName(project.getProjectName());
        dto.setProjectDescription(project.getProjectDescription());
        dto.setProjectType(project.getProjectType());
        dto.setPriority(project.getPriority());
        dto.setVisibility(project.getVisibility());
        project.getProjectMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .ifPresent(member -> dto.setRole(member.getRole()));
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setProjectStatus(project.getProjectStatus());
        dto.setCreatedAt(project.getCreatedAt());
        return dto;
    }
}
