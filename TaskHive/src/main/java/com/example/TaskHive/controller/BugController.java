package com.example.TaskHive.controller;

import com.example.TaskHive.dto.BugGetDto;
import com.example.TaskHive.dto.BugPostDto;
import com.example.TaskHive.dto.BugPutDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.service.service_interface.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BugController
{
    private final BugService bugService;

    @Autowired
    public BugController(BugService bugService)
    {
        this.bugService = bugService;
    }

    @PostMapping("/tasks/{taskId}/bugs")
    public ResponseEntity<ResponseDto> create(
            @PathVariable("taskId") Long taskId,
            @RequestBody BugPostDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(bugService.create(taskId, dto, userDetails), HttpStatus.OK);
    }

    @GetMapping("/tasks/{taskId}/bugs")
    public ResponseEntity<List<BugGetDto>> getAllBugs(
            @PathVariable("taskId") Long taskId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(bugService.getAllBugs(taskId, userDetails), HttpStatus.OK);
    }

    @GetMapping("/tasks/{taskId}/bugs/{bugId}")
    public ResponseEntity<BugGetDto> getBugById(
            @PathVariable("taskId") Long taskId,
            @PathVariable("bugId") Long bugId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(bugService.getBugById(taskId, bugId, userDetails), HttpStatus.OK);
    }

    @PutMapping("/tasks/{taskId}/bugs/{bugId}")
    public ResponseEntity<ResponseDto> update(
            @PathVariable("taskId") Long taskId,
            @PathVariable("bugId") Long bugId,
            @RequestBody BugPutDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(bugService.update(taskId, bugId, dto, userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{taskId}/bugs/{bugId}")
    public ResponseEntity<ResponseDto> delete(
            @PathVariable("taskId") Long taskId,
            @PathVariable("bugId") Long bugId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(bugService.delete(taskId, bugId, userDetails), HttpStatus.OK);
    }

}
