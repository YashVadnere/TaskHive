package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.EpicDto;
import com.example.TaskHive.dto.EpicResponseDto;
import com.example.TaskHive.dto.EpicUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EpicService
{

    ResponseDto createEpic(Long projectId, UserDetails userDetails, EpicDto dto);

    List<EpicResponseDto> getAllEpicsByProjectId(Long projectId, UserDetails userDetails);

    ResponseDto updateEpic(Long projectId, Long epicId, EpicUpdateDto dto, UserDetails userDetails);

    ResponseDto deleteEpic(Long projectId, Long epicId, UserDetails userDetails);
}
