package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ProjectResponseDto;
import com.example.TaskHive.service.service_interface.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProjectMemberController
{
    private final ProjectMemberService projectMemberService;

    @Autowired
    public ProjectMemberController(ProjectMemberService projectMemberService)
    {
        this.projectMemberService = projectMemberService;
    }

//    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(projectMemberService.getAllProjects(userId), HttpStatus.OK);
    }

}
