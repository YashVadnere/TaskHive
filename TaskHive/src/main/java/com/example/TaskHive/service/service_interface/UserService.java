package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.UserResponseDto;
import com.example.TaskHive.dto.UserSearchDto;
import com.example.TaskHive.dto.UserUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService
{

    UserResponseDto getUserById(Long userId);

    ResponseEntity<byte[]> getProfilePictureById(Long userId);

    ResponseDto updateProfile(UserDetails userDetails, UserUpdateDto dto);

    ResponseDto updateProfilePicture(UserDetails userDetails, MultipartFile file);

    ResponseDto deleteProfilePicture(UserDetails userDetails);

    List<UserSearchDto> search(String fullName);
}
