package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.config.JwtAuthenticationService;
import com.example.TaskHive.dto.*;
import com.example.TaskHive.entity.ActivePlan;
import com.example.TaskHive.entity.ProfilePicture;
import com.example.TaskHive.entity.Token;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.*;
import com.example.TaskHive.repository.ProfilePictureRepository;
import com.example.TaskHive.repository.TokenRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.EmailService;
import com.example.TaskHive.service.service_interface.UserAuthService;
import jakarta.transaction.Transactional;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserAuthServiceImplementation implements UserAuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfilePictureRepository profilePictureRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserAuthServiceImplementation(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ProfilePictureRepository profilePictureRepository,
            EmailService emailService,
            AuthenticationManager authenticationManager,
            JwtAuthenticationService jwtAuthenticationService,
            TokenRepository tokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profilePictureRepository = profilePictureRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public ResponseDto signUp(UserSignUpDto dto, MultipartFile file)
    {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());

        if(optionalUser.isPresent())
        {
            throw new UserAlreadyRegistered("Email already registered");
        }

        User user = signUpDtoMapper(dto, file);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(2));
        sendVerificationCode(user);
        userRepository.save(user);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("User registered successfully! A verification code has been sent to your email.");
        return responseDto;
    }

    @Transactional
    @Override
    public ResponseDto verify(UserVerifyDto dto)
    {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if(optionalUser.isPresent())
        {
            User user = optionalUser.get();
            if(user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()))
            {
                if(user.getVerificationCode().equals(dto.getVerificationCode()))
                {
                    user.setVerificationCode(null);
                    user.setVerificationCodeExpiresAt(null);
                    user.setEnabled(true);

                    userRepository.save(user);

                    ResponseDto responseDto = new ResponseDto();
                    responseDto.setMessage("Verification Successful. Account has been activated!!!");

                    return responseDto;
                }
                throw new IncorrectVerificationCode("Incorrect verification code. Try again!!!");
            }
            throw new VerificationCodeExpired("Verification code has expired");
        }
        throw new ResourceNotFound("User not found");

    }

    @Override
    @Transactional
    public ResponseTokenDto signIn(UserSignInDto dto)
    {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());

        if(optionalUser.isPresent())
        {
            User user = optionalUser.get();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword())
            );

            if(authentication.isAuthenticated() && user.isEnabled())
            {
                String jwtToken = jwtAuthenticationService.generateToken(user);
                revokeAllTokens(user);
                saveToken(jwtToken,user);
                ResponseTokenDto responseTokenDto = new ResponseTokenDto();
                responseTokenDto.setJwtToken(jwtToken);
                responseTokenDto.setMessage("Log-in successful");

                return responseTokenDto;
            }

            throw new InvalidCredentials("Invalid username or password");
        }

        throw new ResourceNotFound("User not found");

    }

    private void saveToken(String jwtToken, User user)
    {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setLoggedOut(false);
        token.setUser(user);

        if (user.getTokens() == null)
        {
            user.setTokens(new ArrayList<>());
        }

        user.getTokens().add(token);
        userRepository.save(user);
        tokenRepository.save(token);
    }

    private void revokeAllTokens(User user)
    {
        List<Token> tokenList = tokenRepository.findAllByUser_UserId(user.getUserId());
        if(!tokenList.isEmpty())
        {
            tokenList.forEach(token -> token.setLoggedOut(true));
            tokenRepository.saveAll(tokenList);
        }
    }

    @Override
    public ResponseDto reverify(String email)
    {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent())
        {
            User user = optionalUser.get();
            if(user.isEnabled())
            {
                throw new UserAlreadyVerified("User is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(2));
            userRepository.save(user);
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage("New verification code sent");
            return responseDto;
        }

        throw new ResourceNotFound("User not found");
    }

    @Transactional
    private User signUpDtoMapper(UserSignUpDto dto, MultipartFile file)
    {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setJobTitle(dto.getJobTitle());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNoOfProjects(0L);
        user.setActivePlan(ActivePlan.FREE);
        user.setLastLogin(null);
        user.setEnabled(false);

        user = userRepository.save(user);

        if(file!=null && !file.isEmpty())
        {
            try
            {
                ProfilePicture profilePicture = new ProfilePicture();
                profilePicture.setFileName(file.getOriginalFilename());
                Tika tika = new Tika();
                profilePicture.setFileType(tika.detect(file.getInputStream()));
                profilePicture.setImage(file.getBytes());

                String downloadUrl = "http://localhost:8080/api/v1/user/"+user.getUserId()+"/profile-picture";
                profilePicture.setDownloadUrl(downloadUrl);

                user.setProfilePicture(profilePicture);
                profilePicture.setUser(user);

                profilePictureRepository.save(profilePicture);

            } catch (IOException e) {
                throw new ResourceNotFound("Profile not found");
            }
        }

        return user;
    }

    private String generateVerificationCode()
    {
        SecureRandom random = new SecureRandom();
        long verificationCode=random.nextInt(900000)+100000;
        return String.valueOf(Math.abs(verificationCode));
    }

    private void sendVerificationCode(User user)
    {
        String to = user.getEmail();
        String subject = "Account Verification";
        String htmlText = "<html>\n" +
                "  <body>\n" +
                "    <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "      <tr>\n" +
                "        <td align=\"center\">\n" +
                "          <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"10\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\">\n" +
                "                <h2>Welcome, <strong>"+user.getFirstName()+" "+ user.getLastName()+"</strong> </h2>\n" +
                "                <p>Thank you for signing up! Please use the verification code below to verify your email address.</p>\n" +
                "                <h3>Your Verification Code:</h3>\n" +
                "                <h1>"+user.getVerificationCode()+"</h1>\n" +
                "                <p>This code will expire in 2 minutes. If you did not request this, please ignore this email.</p>\n" +
                "                <p>Best Regards, <br> TaskHive Team</p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>\n";

        emailService.sendEmailToUser(to, subject, htmlText);
    }
}
