package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.UserResponseDto;
import com.example.TaskHive.dto.UserSearchDto;
import com.example.TaskHive.dto.UserUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService
{

    UserResponseDto getUserById(Long userId);

    ResponseEntity<byte[]> getProfilePictureById(Long userId);

    ResponseDto updateProfile(Long userId, UserUpdateDto dto);

    ResponseDto updateProfilePicture(Long userId, MultipartFile file);

    ResponseDto deleteProfilePicture(Long userId);

    List<UserSearchDto> search(String fullName);
}
