package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ProjectResponseDto;
import com.example.TaskHive.dto.ProjectUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.Project;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService
{

    ResponseDto createProject(Long userId, ProjectPostDto dto);

    List<ProjectResponseDto> getAllProjects(Long userId);

    ProjectResponseDto getProjectById(Long userId, Long projectId);

    ResponseDto deleteProjectById(Long userId, Long projectId);

    ResponseDto updateById(Long userId, Long projectId, ProjectUpdateDto dto);

    List<ProjectResponseDto> search(Long userId, String projectName);

    ResponseDto leaveProjectById(Long userId, Long projectId);

    ResponseDto removeTeamMember(Long projectId, Long memberId, UserDetails userDetails);
}
