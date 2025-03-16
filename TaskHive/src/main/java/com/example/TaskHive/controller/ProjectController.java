package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ProjectResponseDto;
import com.example.TaskHive.dto.ProjectUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.service_interface.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/users/{userId}/projects")
    public ResponseEntity<ResponseDto> createProject(
            @PathVariable("userId") Long userId,
            @RequestBody ProjectPostDto dto
    ) {
        return new ResponseEntity<>(projectService.createProject(userId, dto), HttpStatus.OK);
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

    @PutMapping("/users/{userId}/projects/{projectId}")
    public ResponseEntity<ResponseDto> updateById(
            @PathVariable("userId") Long userId,
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectUpdateDto dto
            ) {
        return new ResponseEntity<>(projectService.updateById(userId, projectId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/projects/{projectId}")
    public ResponseEntity<ResponseDto> deleteProjectById(
            @PathVariable("userId") Long userId,
            @PathVariable("projectId") Long projectId
    ) {
        return new ResponseEntity<>(projectService.deleteProjectById(userId, projectId),HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/projects/{projectId}/memberships")
    public ResponseEntity<ResponseDto> leaveProjectById(
            @PathVariable("userId") Long userId,
            @PathVariable("projectId") Long projectId
    ) {
        return new ResponseEntity<>(projectService.leaveProjectById(userId, projectId), HttpStatus.OK);
    }

}
