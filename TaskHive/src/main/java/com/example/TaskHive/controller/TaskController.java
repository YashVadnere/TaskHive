package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.TaskGetDto;
import com.example.TaskHive.dto.TaskPostDto;
import com.example.TaskHive.dto.TaskPutDto;
import com.example.TaskHive.service.service_interface.TaskService;
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
public class TaskController
{
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService)
    {
        this.taskService = taskService;
    }

    @PostMapping("/stories/{storiesId}/tasks")
    public ResponseEntity<ResponseDto> create(
            @PathVariable("storiesId") Long storiesId,
            @RequestBody TaskPostDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(taskService.create(storiesId, dto, userDetails), HttpStatus.OK);
    }

    @GetMapping("/stories/{storiesId}/tasks")
    public ResponseEntity<List<TaskGetDto>> getAllTasks(
            @PathVariable("storiesId") Long storiesId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(taskService.getAllTasks(storiesId, userDetails), HttpStatus.OK);
    }

    @GetMapping("/stories/{storiesId}/tasks/{taskId}")
    public ResponseEntity<TaskGetDto> getTaskById(
            @PathVariable("storiesId") Long storiesId,
            @PathVariable("taskId") Long taskId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(taskService.getTaskById(storiesId, taskId, userDetails), HttpStatus.OK);
    }

    @PutMapping("/stories/{storiesId}/tasks/{taskId}")
    public ResponseEntity<ResponseDto> update(
            @PathVariable("storiesId") Long storiesId,
            @PathVariable("taskId") Long taskId,
            @RequestBody TaskPutDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(taskService.update(storiesId, taskId, dto, userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/stories/{storiesId}/tasks/{taskId}")
    public ResponseEntity<ResponseDto> deleteTaskById(
            @PathVariable("storiesId") Long storiesId,
            @PathVariable("taskId") Long taskId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(taskService.deleteTaskById(storiesId, taskId, userDetails), HttpStatus.OK);
    }

}
