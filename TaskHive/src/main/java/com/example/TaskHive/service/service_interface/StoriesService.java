package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.StoriesGetDto;
import com.example.TaskHive.dto.StoriesPostDto;
import com.example.TaskHive.dto.StoriesPutDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StoriesService
{

    ResponseDto create(Long epicId, StoriesPostDto dto, UserDetails userDetails);

    List<StoriesGetDto> getAllStories(Long epicId, UserDetails userDetails);

    StoriesGetDto getStoriesById(Long epicId, Long storiesId, UserDetails userDetails);

    ResponseDto updateStoriesById(Long epicId, Long storiesId, StoriesPutDto dto, UserDetails userDetails);

    ResponseDto deleteStoriesById(Long epicId, Long storiesId, UserDetails userDetails);
}
