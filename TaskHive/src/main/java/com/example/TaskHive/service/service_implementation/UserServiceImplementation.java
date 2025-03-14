package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.UserResponseDto;
import com.example.TaskHive.dto.UserSearchDto;
import com.example.TaskHive.dto.UserUpdateDto;
import com.example.TaskHive.entity.ProfilePicture;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.*;
import com.example.TaskHive.repository.ProfilePictureRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.UserService;
import jakarta.transaction.Transactional;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService
{

    private final UserRepository userRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImplementation(
            UserRepository userRepository,
            ProfilePictureRepository profilePictureRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.profilePictureRepository = profilePictureRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto getUserById(Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        return mapUserEntityToUserResponseDto(user);
    }

    @Override
    public ResponseEntity<byte[]> getProfilePictureById(Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        ProfilePicture profilePicture = profilePictureRepository.findById(user.getProfilePicture().getProfilePictureId())
                .orElseThrow(() -> new ResourceNotFound("Profile picture not found"));

        byte[] imageFile = profilePicture.getImage();
        String imageType = profilePicture.getFileType();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(imageType));

        return new ResponseEntity<>(imageFile, httpHeaders, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseDto updateProfile(Long userId, UserUpdateDto dto)
    {
        User user = mapUserUpdateDtoToUserEntity(userId, dto);

        userRepository.save(user);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Profile updated successfully");

        return responseDto;
    }

    @Override
    public ResponseDto updateProfilePicture(Long userId, MultipartFile file)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        ProfilePicture profilePicture = user.getProfilePicture();

        if(profilePicture==null)
        {
            profilePicture = new ProfilePicture();
        }

        if(file!=null && !file.isEmpty())
        {
            try
            {
                profilePicture.setFileName(file.getOriginalFilename());
                Tika tika = new Tika();
                profilePicture.setFileType(tika.detect(file.getInputStream()));
                profilePicture.setImage(file.getBytes());

                if(profilePicture.getDownloadUrl()==null || profilePicture.getDownloadUrl().isEmpty())
                {
                    String downloadUrl = "http://localhost:8080/api/v1/users/"+userId+"/profile-picture";
                    profilePicture.setDownloadUrl(downloadUrl);
                }

                if(profilePicture.getUser()==null)
                {
                    profilePicture.setUser(user);
                    user.setProfilePicture(profilePicture);
                }

                profilePictureRepository.save(profilePicture);
                userRepository.save(user);
                ResponseDto responseDto = new ResponseDto();
                responseDto.setMessage("Profile picture updated successfully");
                return responseDto;

            } catch (Exception e) {
                throw new FileStorageException("Error processing profile picture upload");
            }

        }
        throw new BadRequestException("Profile picture is required");
    }

    @Override
    @Transactional
    public ResponseDto deleteProfilePicture(Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        ProfilePicture profilePicture = user.getProfilePicture();

        if(profilePicture!=null)
        {
            user.setProfilePicture(null);
            profilePictureRepository.deleteById(profilePicture.getProfilePictureId());
            userRepository.save(user);
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage("Profile picture deleted successfully");
            return responseDto;
        }
        throw new ResourceNotFound("Profile picture not found");
    }

    @Override
    public List<UserSearchDto> search(String fullName)
    {
        List<User> users = userRepository.findAllByFullNameContainingIgnoreCase(fullName);

        return users.stream()
                .map(this::mapUserEntityToUserSearchDto)
                .collect(Collectors.toList());

    }

    private UserSearchDto mapUserEntityToUserSearchDto(User user)
    {
        UserSearchDto dto = new UserSearchDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setJobTitle(user.getJobTitle());

        if(user.getProfilePicture()!=null)
        {
            dto.setImageUrl(user.getProfilePicture().getDownloadUrl());
        }
        else
        {
            dto.setImageUrl(null);
        }

        return dto;
    }

    private User mapUserUpdateDtoToUserEntity(Long userId, UserUpdateDto dto)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        if(dto.getFirstName()!=null && !dto.getFirstName().isEmpty())
        {
            user.setFirstName(dto.getFirstName());
            user.setFullName(user.getFirstName()+" "+user.getLastName());
        }
        if(dto.getLastName()!=null && !dto.getLastName().isEmpty())
        {
            user.setLastName(dto.getLastName());
            user.setFullName(user.getFirstName()+" "+user.getLastName());
        }
        if(dto.getJobTitle()!=null && !dto.getJobTitle().isEmpty())
        {
            user.setJobTitle(dto.getJobTitle());
        }
        if(dto.getCurrentPassword()!=null && !dto.getCurrentPassword().isEmpty())
        {
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
            {
                throw new InvalidCredentials("Incorrect password");
            }
            if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty() ||
                    dto.getConfirmPassword() == null || dto.getConfirmPassword().isEmpty())
            {
                throw new ResourceNotFound("Enter new and confirm password");
            }
            if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            {
                throw new Mismatch("New password and confirm password should be the same");
            }
            if (!passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            {
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }
        }
        return user;
    }

    private UserResponseDto mapUserEntityToUserResponseDto(User user)
    {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setJobTitle(user.getJobTitle());
        dto.setEmail(user.getEmail());
        dto.setNoOfProjects(user.getNoOfProjects());
        dto.setActivePlan(user.getActivePlan());
        dto.setLastLogin(user.getLastLogin());

        if(user.getProfilePicture() != null)
        {
            dto.setImageUrl(user.getProfilePicture().getDownloadUrl());
        }
        else
        {
            dto.setImageUrl(null);
        }
        return dto;
    }
}
