package com.example.TaskHive.controller;

import com.example.TaskHive.dto.EpicDto;
import com.example.TaskHive.dto.EpicResponseDto;
import com.example.TaskHive.dto.EpicUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.ProjectSecurityService;
import com.example.TaskHive.service.service_interface.EpicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EpicController
{
    private final EpicService epicService;
    private final ProjectSecurityService projectSecurityService;

    @Autowired
    public EpicController(
            EpicService epicService,
            ProjectSecurityService projectSecurityService
    ) {
        this.epicService = epicService;
        this.projectSecurityService = projectSecurityService;
    }

    @GetMapping("/projects/{projectId}/epics")
    public ResponseEntity<List<EpicResponseDto>> getAllEpicsByProjectId(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(epicService.getAllEpicsByProjectId(projectId, userDetails), HttpStatus.OK);
    }

    @PostMapping("/projects/{projectId}/epics")
    @PreAuthorize("@projectSecurityService.hasRoleInProject(#projectId, principal, 'SCRUM_MASTER')")
    public ResponseEntity<ResponseDto> createEpic(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EpicDto dto
    ) {
        return new ResponseEntity<>(epicService.createEpic(projectId, userDetails, dto), HttpStatus.OK);
    }

    @PutMapping("/projects/{projectId}/epics/{epicId}")
    @PreAuthorize("@projectSecurityService.hasRoleInProject(#projectId, principal, 'SCRUM_MASTER')")
    public ResponseEntity<ResponseDto> updateEpic(
            @PathVariable("projectId") Long projectId,
            @PathVariable("epicId") Long epicId,
            @RequestBody EpicUpdateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(epicService.updateEpic(projectId, epicId, dto, userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/epics/{epicId}")
    public ResponseEntity<ResponseDto> deleteEpic(
            @PathVariable("projectId") Long projectId,
            @PathVariable("epicId") Long epicId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(epicService.deleteEpic(projectId, epicId, userDetails), HttpStatus.OK);
    }

}
