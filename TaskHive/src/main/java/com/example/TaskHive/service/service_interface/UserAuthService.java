package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserAuthService
{

    ResponseDto signUp(UserSignUpDto dto, MultipartFile file);

    ResponseDto verify(UserVerifyDto dto);

    ResponseTokenDto signIn(UserSignInDto dto);

    ResponseDto reverify(String email);
}
