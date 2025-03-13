package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ProjectUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.Project;
import com.example.TaskHive.entity.ProjectStatus;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.ProjectLimitExceeded;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.ProjectService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImplementation implements ProjectService
{
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImplementation(
            UserRepository userRepository,
            ProjectRepository projectRepository
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public ResponseDto createProject(Long userId, ProjectPostDto dto)
    {
        User user =userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFound("User not found"));

        if(user.getNoOfProjects() >= user.getProjectLimit())
        {
            throw new ProjectLimitExceeded("Project limit exceeded");
        }

        user.setNoOfProjects(user.getNoOfProjects()+1);
        Project project = mapProjectPostDtoToEntity(dto);
        project.setUser(user);
        user.getProjects().add(project);

        projectRepository.save(project);
        userRepository.save(user);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Project created");
        return responseDto;
    }

    @Override
    @Transactional
    public List<Project> getAllProjects(Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        return user.getProjects();
    }

    @Override
    @Transactional
    public Project getProjectById(Long userId, Long projectId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(userId))
        {
            throw new ResourceNotFound("Project not found for this user");
        }

        return project;
    }

    @Override
    @Transactional
    public ResponseDto deleteProjectById(Long userId, Long projectId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(userId))
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
    public ResponseDto updateById(Long userId, Long projectId, ProjectUpdateDto dto)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(userId))
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
    public List<Project> search(Long userId, String projectName)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        return projectRepository.findAllByUser_UserIdAndProjectNameContainingIgnoreCase(userId, projectName);
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

}
