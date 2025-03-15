package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.UserResponseDto;
import com.example.TaskHive.dto.UserSearchDto;
import com.example.TaskHive.dto.UserUpdateDto;
import com.example.TaskHive.service.service_interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/profile-picture")
    public ResponseEntity<byte[]> getProfilePictureById(@PathVariable("userId") Long userId)
    {
        return userService.getProfilePictureById(userId);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserSearchDto>> search(@RequestParam String fullName)
    {
        return new ResponseEntity<>(userService.search(fullName), HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ResponseDto> updateProfile(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateDto dto
    ) {
        return new ResponseEntity<>(userService.updateProfile(userId, dto), HttpStatus.OK);
    }

    @PutMapping("/users/{userId}/profile-picture")
    public ResponseEntity<ResponseDto> updateProfilePicture(
            @PathVariable("userId") Long userId,
            @RequestPart MultipartFile file
    ) {
        return new ResponseEntity<>(userService.updateProfilePicture(userId, file), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/profile-picture")
    public ResponseEntity<ResponseDto> deleteProfilePicture(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(userService.deleteProfilePicture(userId), HttpStatus.OK);
    }

}
