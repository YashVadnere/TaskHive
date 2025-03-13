package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.Project;
import com.example.TaskHive.entity.ProjectStatus;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public ResponseDto createProject(Long userId, ProjectPostDto dto)
    {
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty())
        {
            throw new ResourceNotFound("User not found");
        }

        User user = optionalUser.get();
        Project project = mapProjectPostDtoToEntity(dto);
        project.setUser(user);
        user.getProjects().add(project);

        userRepository.save(user);
        projectRepository.save(project);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Project created");
        return responseDto;
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
