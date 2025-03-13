package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ProjectService
{

    ResponseDto createProject(Long userId, ProjectPostDto dto);
}
