package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ProjectResponseDto;
import com.example.TaskHive.entity.Project;
import com.example.TaskHive.entity.ProjectMember;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProjectMemberRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImplementation implements ProjectMemberService
{
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectMemberServiceImplementation(
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository
    ) {
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ProjectResponseDto> getAllProjects(Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        List<Project> projects = user.getProjectMembers()
                .stream()
                .map(ProjectMember::getProject)
                .distinct()
                .toList();

        return List.of();
    }
}
