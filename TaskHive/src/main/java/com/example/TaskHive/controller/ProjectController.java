package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ProjectUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.Project;
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

    @PostMapping("/users/{user-id}/projects")
    public ResponseEntity<ResponseDto> createProject(
            @PathVariable("user-id") Long userId,
            @RequestBody ProjectPostDto dto
    ) {
        return new ResponseEntity<>(projectService.createProject(userId, dto), HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}/projects")
    public ResponseEntity<List<Project>> getAllProjects(@PathVariable("user-id") Long userId)
    {
        return new ResponseEntity<>(projectService.getAllProjects(userId),HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}/projects/{project-id}")
    public ResponseEntity<Project> getProjectById(
            @PathVariable("user-id") Long userId,
            @PathVariable("project-id")Long projectId
    ) {
        return new ResponseEntity<>(projectService.getProjectById(userId, projectId),HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}/projects/search")
    public ResponseEntity<List<Project>> search(
            @PathVariable("user-id") Long userId,
            @RequestParam String projectName
    ) {
        return new ResponseEntity<>(projectService.search(userId, projectName),HttpStatus.OK);
    }

    @PutMapping("/users/{user-id}/projects/{project-id}")
    public ResponseEntity<ResponseDto> updateById(
            @PathVariable("user-id") Long userId,
            @PathVariable("project-id") Long projectId,
            @RequestBody ProjectUpdateDto dto
            ) {
        return new ResponseEntity<>(projectService.updateById(userId, projectId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/users/{user-id}/projects/{project-id}")
    public ResponseEntity<ResponseDto> deleteProjectById(
            @PathVariable("user-id") Long userId,
            @PathVariable("project-id") Long projectId
    ) {
        return new ResponseEntity<>(projectService.deleteProjectById(userId, projectId),HttpStatus.OK);
    }

}
