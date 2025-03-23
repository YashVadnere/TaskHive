package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.StoriesGetDto;
import com.example.TaskHive.dto.StoriesPostDto;
import com.example.TaskHive.dto.StoriesPutDto;
import com.example.TaskHive.service.service_interface.StoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StoriesController
{
    private final StoriesService storiesService;

    @Autowired
    public StoriesController(StoriesService storiesService)
    {
        this.storiesService = storiesService;
    }

    @PostMapping("/epics/{epicId}/stories")
    public ResponseEntity<ResponseDto> create(
            @PathVariable("epicId") Long epicId,
            @RequestBody StoriesPostDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(storiesService.create(epicId, dto, userDetails), HttpStatus.OK);
    }

    @GetMapping("/epics/{epicId}/stories")
    public ResponseEntity<List<StoriesGetDto>> getAllStories(
            @PathVariable("epicId") Long epicId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(storiesService.getAllStories(epicId, userDetails), HttpStatus.OK);
    }

    @GetMapping("/epics/{epicId}/stories/{storiesId}")
    public ResponseEntity<StoriesGetDto> getStoriesById(
            @PathVariable("epicId") Long epicId,
            @PathVariable("storiesId") Long storiesId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(storiesService.getStoriesById(epicId, storiesId, userDetails), HttpStatus.OK);
    }

    @PutMapping("/epics/{epicId}/stories/{storiesId}")
    public ResponseEntity<ResponseDto> updateStoriesById(
            @PathVariable("epicId") Long epicId,
            @PathVariable("storiesId") Long storiesId,
            @RequestBody StoriesPutDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(storiesService.updateStoriesById(epicId, storiesId, dto, userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/epics/{epicId}/stories/{storiesId}")
    public ResponseEntity<ResponseDto> deleteStoriesById(
            @PathVariable("epicId") Long epicId,
            @PathVariable("storiesId") Long storiesId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(storiesService.deleteStoriesById(epicId, storiesId, userDetails), HttpStatus.OK);
    }

}
