package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.BugGetDto;
import com.example.TaskHive.dto.BugPostDto;
import com.example.TaskHive.dto.BugPutDto;
import com.example.TaskHive.dto.ResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BugService 
{

    ResponseDto create(Long taskId, BugPostDto dto, UserDetails userDetails);

    List<BugGetDto> getAllBugs(Long taskId, UserDetails userDetails);

    BugGetDto getBugById(Long taskId, Long bugId, UserDetails userDetails);

    ResponseDto update(Long taskId, Long bugId, BugPutDto dto, UserDetails userDetails);

    ResponseDto delete(Long taskId, Long bugId, UserDetails userDetails);
}
