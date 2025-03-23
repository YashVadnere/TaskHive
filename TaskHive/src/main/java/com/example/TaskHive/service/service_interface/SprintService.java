package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.SprintGetDto;
import com.example.TaskHive.dto.SprintPostDto;
import com.example.TaskHive.dto.SprintPutDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SprintService
{

    ResponseDto create(Long projectId, UserDetails userDetails, SprintPostDto dto);

    List<SprintGetDto> getAllSprints(Long projectId, UserDetails userDetails);

    SprintGetDto getSprintById(Long projectId, Long sprintId, UserDetails userDetails);

    ResponseDto update(Long projectId, Long sprintId, SprintPutDto dto, UserDetails userDetails);

    ResponseDto delete(Long projectId, Long sprintId, UserDetails userDetails);
}
