package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.SprintGetDto;
import com.example.TaskHive.dto.SprintPostDto;
import com.example.TaskHive.dto.SprintPutDto;
import com.example.TaskHive.service.service_interface.SprintService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SprintController
{
    private final SprintService sprintService;

    @Autowired
    public SprintController(SprintService sprintService)
    {
        this.sprintService = sprintService;
    }

    @PostMapping("/projects/{projectId}/sprints")
    public ResponseEntity<ResponseDto> create(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SprintPostDto dto
    ) {
        return new ResponseEntity<>(sprintService.create(projectId, userDetails, dto), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/sprints")
    public ResponseEntity<List<SprintGetDto>> getAllSprints(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(sprintService.getAllSprints(projectId, userDetails), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/sprints/{sprintId}")
    public ResponseEntity<SprintGetDto> getSprintById(
            @PathVariable("projectId") Long projectId,
            @PathVariable("sprintId") Long sprintId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(sprintService.getSprintById(projectId, sprintId, userDetails), HttpStatus.OK);
    }

    @PutMapping("/projects/{projectId}/sprints/{sprintId}")
    public ResponseEntity<ResponseDto> update(
            @PathVariable("projectId") Long projectId,
            @PathVariable("sprintId") Long sprintId,
            @RequestBody SprintPutDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(sprintService.update(projectId, sprintId, dto, userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/sprints/{sprintId}")
    public ResponseEntity<ResponseDto> delete(
            @PathVariable("projectId") Long projectId,
            @PathVariable("sprintId") Long sprintId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(sprintService.delete(projectId, sprintId, userDetails), HttpStatus.OK);
    }

}
