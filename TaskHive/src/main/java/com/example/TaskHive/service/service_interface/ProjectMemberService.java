package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ProjectResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectMemberService
{

    List<ProjectResponseDto> getAllProjects(Long userId);
}
