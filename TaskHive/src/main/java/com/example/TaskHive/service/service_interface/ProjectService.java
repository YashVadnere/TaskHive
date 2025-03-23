package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.*;
import com.example.TaskHive.entity.Project;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService
{

    ResponseDto createProject(UserDetails userDetails, ProjectPostDto dto);

    List<ProjectResponseDto> getAllProjects(Long userId);

    ProjectResponseDto getProjectById(Long userId, Long projectId);

    ResponseDto deleteProjectById(UserDetails userDetails, Long projectId);

    ResponseDto updateById(UserDetails userDetails, Long projectId, ProjectUpdateDto dto);

    List<ProjectResponseDto> search(Long userId, String projectName);

    ResponseDto leaveProjectById(UserDetails userDetails, Long projectId);

    ResponseDto removeTeamMember(Long projectId, Long memberId, UserDetails userDetails);

    List<ProjectMemberDto> getAllProjectMembers(Long projectId, UserDetails userDetails);
}
