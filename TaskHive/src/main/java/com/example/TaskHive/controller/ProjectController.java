package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ProjectResponseDto;
import com.example.TaskHive.dto.ProjectUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.service_interface.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProjectController
{
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService)
    {
        this.projectService = projectService;
    }

    @PostMapping("/projects")
    public ResponseEntity<ResponseDto> createProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectPostDto dto
    ) {
        return new ResponseEntity<>(projectService.createProject(userDetails, dto), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(projectService.getAllProjects(userId),HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/projects/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @PathVariable("userId") Long userId,
            @PathVariable("projectId")Long projectId
    ) {
        return new ResponseEntity<>(projectService.getProjectById(userId, projectId),HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/projects/search")
    public ResponseEntity<List<ProjectResponseDto>> search(
            @PathVariable("userId") Long userId,
            @RequestParam String projectName
    ) {
        return new ResponseEntity<>(projectService.search(userId, projectName),HttpStatus.OK);
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ResponseDto> updateById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectUpdateDto dto
            ) {
        return new ResponseEntity<>(projectService.updateById(userDetails, projectId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<ResponseDto> deleteProjectById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("projectId") Long projectId
    ) {
        return new ResponseEntity<>(projectService.deleteProjectById(userDetails, projectId),HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/memberships")
    public ResponseEntity<ResponseDto> leaveProjectById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("projectId") Long projectId
    ) {
        return new ResponseEntity<>(projectService.leaveProjectById(userDetails, projectId), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/members/{memberId}")
    public ResponseEntity<ResponseDto> removeTeamMember(
            @PathVariable("projectId") Long projectId,
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(projectService.removeTeamMember(projectId, memberId, userDetails), HttpStatus.OK);
    }

}
