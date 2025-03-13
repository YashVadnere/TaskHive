package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ProjectPostDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.service_interface.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/user/{user-id}/project")
    public ResponseEntity<ResponseDto> createProject(
            @PathVariable("user-id") Long userId,
            @RequestBody ProjectPostDto dto
    ) {
        return new ResponseEntity<>(projectService.createProject(userId, dto), HttpStatus.OK);
    }


}
