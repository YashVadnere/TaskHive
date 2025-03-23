package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.TaskGetDto;
import com.example.TaskHive.dto.TaskPostDto;
import com.example.TaskHive.dto.TaskPutDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService
{

    ResponseDto create(Long storiesId, TaskPostDto dto, UserDetails userDetails);

    List<TaskGetDto> getAllTasks(Long storiesId, UserDetails userDetails);

    TaskGetDto getTaskById(Long storiesId, Long taskId, UserDetails userDetails);

    ResponseDto update(Long storiesId, Long taskId, TaskPutDto dto, UserDetails userDetails);

    ResponseDto deleteTaskById(Long storiesId, Long taskId, UserDetails userDetails);
}
