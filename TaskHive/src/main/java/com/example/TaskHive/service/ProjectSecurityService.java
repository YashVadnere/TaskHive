package com.example.TaskHive.service;

import com.example.TaskHive.entity.Project;
import com.example.TaskHive.entity.ProjectMember;
import com.example.TaskHive.entity.Role;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ProjectSecurityService
{
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectSecurityService(
            UserRepository userRepository,
            ProjectRepository projectRepository
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public boolean hasRoleInProject(Long projectId, UserDetails userDetails, String requiredRole)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to the user");
        }

        ProjectMember projectMember = project.getProjectMembers()
                .stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("Project member not found"));

        return projectMember.getRole()==Role.valueOf(requiredRole);

    }

}
