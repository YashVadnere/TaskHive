package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.StoriesGetDto;
import com.example.TaskHive.dto.StoriesPostDto;
import com.example.TaskHive.dto.StoriesPutDto;
import com.example.TaskHive.entity.Epic;
import com.example.TaskHive.entity.Status;
import com.example.TaskHive.entity.Stories;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.EpicRepository;
import com.example.TaskHive.repository.StoriesRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.StoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoriesServiceImplementation implements StoriesService
{
    private final StoriesRepository storiesRepository;
    private final UserRepository userRepository;
    private final EpicRepository epicRepository;

    @Autowired
    public StoriesServiceImplementation(
            StoriesRepository storiesRepository,
            UserRepository userRepository,
            EpicRepository epicRepository
    ) {
        this.storiesRepository = storiesRepository;
        this.userRepository = userRepository;
        this.epicRepository = epicRepository;
    }

    @Override
    public ResponseDto create(Long epicId, StoriesPostDto dto, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        if(!epic.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Epic does not belong to user");
        }

        Stories stories = mapStoriesPostDtoToStoriesEntity(dto);
        stories.setStoriesStatus(Status.TO_DO);
        stories.setCreatedAt(LocalDateTime.now());
        stories.setUpdatedAt(LocalDateTime.now());
        stories.setEpic(epic);
        stories.setUser(user);
        storiesRepository.save(stories);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Story created successfully");
        return responseDto;
    }

    @Override
    public List<StoriesGetDto> getAllStories(Long epicId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        if(!epic.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Epic does not belong to user");
        }

        return storiesRepository.findAllByEpic(epic)
                .stream()
                .map(this::mapStoriesEntityToStoriesGetDto)
                .toList();
    }

    @Override
    public StoriesGetDto getStoriesById(Long epicId, Long storiesId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        if(!epic.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Epic does not belong to user");
        }

        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        if(!stories.getEpic().getEpicId().equals(epicId))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        return mapStoriesEntityToStoriesGetDto(stories);
    }

    @Override
    public ResponseDto updateStoriesById(Long epicId, Long storiesId, StoriesPutDto dto, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        if(!epic.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Epic does not belong to user");
        }

        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        if(!stories.getEpic().getEpicId().equals(epicId))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        if(StringUtils.hasText(dto.getTitle()))
        {
            stories.setTitle(dto.getTitle());
        }
        if(StringUtils.hasText(dto.getDescription()))
        {
            stories.setDescription(dto.getDescription());
        }
        if(StringUtils.hasText(dto.getStoriesPriority()))
        {
            stories.setStoriesPriority(dto.getStoriesPriority());
        }
        if(dto.getStoriesPoint() != null)
        {
            stories.setStoriesPoint(dto.getStoriesPoint());
        }
        if(dto.getStoriesStatus() != null)
        {
            stories.setStoriesStatus(dto.getStoriesStatus());
        }

        storiesRepository.save(stories);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Story updated successfully");
        return responseDto;
    }

    @Override
    public ResponseDto deleteStoriesById(Long epicId, Long storiesId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        if(!epic.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Epic does not belong to user");
        }

        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        if(!stories.getEpic().getEpicId().equals(epicId))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        storiesRepository.deleteById(stories.getStoriesId());

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Story deleted successfully");
        return responseDto;
    }

    private StoriesGetDto mapStoriesEntityToStoriesGetDto(Stories stories)
    {
        StoriesGetDto dto = new StoriesGetDto();
        dto.setStoriesId(stories.getStoriesId());
        dto.setTitle(stories.getTitle());
        dto.setDescription(stories.getDescription());
        dto.setStoriesPriority(stories.getStoriesPriority());
        dto.setStoriesPoint(stories.getStoriesPoint());
        dto.setCreatedAt(stories.getCreatedAt());
        dto.setUpdatedAt(stories.getUpdatedAt());
        return dto;
    }

    private Stories mapStoriesPostDtoToStoriesEntity(StoriesPostDto dto)
    {
        Stories stories = new Stories();
        stories.setTitle(dto.getTitle());
        stories.setDescription(dto.getDescription());
        stories.setStoriesPriority(dto.getStoriesPriority());
        stories.setStoriesPoint(dto.getStoriesPoint());
        return stories;
    }

}
