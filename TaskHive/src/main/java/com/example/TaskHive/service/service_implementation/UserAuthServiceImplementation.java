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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            User user = optionalUser.get();
            if(!user.isEnabled())
            {
                user.setVerificationCode(generateVerificationCode());
                user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(2));
                sendVerificationCode(user);
                userRepository.save(user);

                ResponseDto responseDto = new ResponseDto();
                responseDto.setMessage("User already registered but not verified. A new OTP has been sent to your email.");
                return responseDto;

            }
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
            if(user.getVerificationCodeExpiresAt() == null || user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()))
            {
                if(Objects.equals(user.getVerificationCode(), dto.getVerificationCode()))
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

                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                return responseTokenDto;
            }
            throw new InvalidCredentials("Invalid username or password");
        }
        throw new ResourceNotFound("User not found");
    }

    @Transactional
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

    @Transactional
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
    @Transactional
    public ResponseDto reverify(UserDetails userDetails)
    {
        Optional<User> optionalUser = userRepository.findByEmail(userDetails.getUsername());

        if(optionalUser.isPresent())
        {
            User user = optionalUser.get();
            if(user.isEnabled())
            {
                throw new UserAlreadyVerified("User is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(2));
            sendVerificationCode(user);
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
        user.setFullName(user.getFirstName()+" "+user.getLastName());
        user.setJobTitle(dto.getJobTitle());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNoOfProjects(0L);
        user.setProjectLimit(3L);
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

                String downloadUrl = "http://localhost:8080/api/v1/users/"+user.getUserId()+"/profile-picture";
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

    @Transactional
    private String generateVerificationCode()
    {
        SecureRandom random = new SecureRandom();
        long verificationCode=random.nextInt(900000)+100000;
        return String.valueOf(Math.abs(verificationCode));
    }

    @Transactional
    private void sendVerificationCode(User user)
    {
        String to = user.getEmail();
        String subject = "Account Verification";
        String htmlText = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>TaskHive | Verify Your Email</title>\n" +
                "\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap\" rel=\"stylesheet\" />\n" +
                "\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Poppins', sans-serif;\n" +
                "            background: #f0f9ff;\n" +
                "            padding: 20px;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 24px;\n" +
                "            border: 1px solid #d1d5db;\n" +
                "            max-width: 600px;\n" +
                "            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);\n" +
                "            overflow: hidden;\n" +
                "            margin: 0 auto;\n" +
                "            display: block;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background: linear-gradient(to right, #60a5fa, #93c5fd);\n" +
                "            color: #ffffff;\n" +
                "            padding: 24px;\n" +
                "            text-align: center;\n" +
                "            border-bottom: 1px solid #bfdbfe;\n" +
                "        }\n" +
                "\n" +
                "        .header h1 {\n" +
                "            font-size: 28px;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        .header p {\n" +
                "            margin: 8px 0 0;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 40px 32px;\n" +
                "            color: #1f2937;\n" +
                "        }\n" +
                "\n" +
                "        .content h2 {\n" +
                "            font-size: 22px;\n" +
                "            color: #2563eb;\n" +
                "            margin-top: 0;\n" +
                "        }\n" +
                "\n" +
                "        .content p {\n" +
                "            font-size: 14px;\n" +
                "            color: #6b7280;\n" +
                "            line-height: 1.5;\n" +
                "        }\n" +
                "\n" +
                "       .code-box {\n" +
                "    background-color: #eff6ff; /* light blue background */\n" +
                "    border: 1px solid #93c5fd; /* soft blue border */\n" +
                "    border-radius: 16px; /* rounded corners */\n" +
                "    text-align: center;\n" +
                "    padding: 24px 32px; /* vertical and horizontal padding */\n" +
                "    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* subtle shadow */\n" +
                "    margin-bottom: 24px; /* space below the box */\n" +
                "    max-width: 400px;\n" +
                "    margin-left: auto;\n" +
                "    margin-right: auto;\n" +
                "}\n" +
                "\n" +
                ".code-box .code-title {\n" +
                "    font-size: 12px; /* small text */\n" +
                "    color: #6b7280; /* gray color */\n" +
                "    text-transform: uppercase;\n" +
                "    margin-bottom: 8px;\n" +
                "    letter-spacing: 0.1em; /* wider spacing */\n" +
                "}\n" +
                "\n" +
                ".code-box .code {\n" +
                "    font-size: 48px; /* large code */\n" +
                "    font-weight: 800;\n" +
                "    letter-spacing: 2px; /* wider letter spacing */\n" +
                "    color: #2563eb; /* bright blue */\n" +
                "}\n" +
                "\n" +
                ".code-box .expire {\n" +
                "    font-size: 12px;\n" +
                "    color: #6b7280; /* gray text */\n" +
                "    margin-top: 12px;\n" +
                "}\n" +
                "\n" +
                "        .expire {\n" +
                "            font-size: 12px;\n" +
                "            color: #6b7280;\n" +
                "            margin-top: 12px;\n" +
                "        }\n" +
                "\n" +
                "        .footer-text {\n" +
                "            text-align: center;\n" +
                "            font-size: 12px;\n" +
                "            color: #9ca3af;\n" +
                "            margin-top: 32px;\n" +
                "            line-height: 1.4;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            background-color: #eff6ff;\n" +
                "            text-align: center;\n" +
                "            padding: 16px 32px;\n" +
                "            font-size: 12px;\n" +
                "            color: #6b7280;\n" +
                "            border-top: 1px solid #e5e7eb;\n" +
                "        }\n" +
                "\n" +
                "        .footer span {\n" +
                "            color: #2563eb;\n" +
                "            font-weight: 600;\n" +
                "            display: block;\n" +
                "            margin-top: 4px;\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 600px) {\n" +
                "            .content {\n" +
                "                padding: 24px 16px;\n" +
                "            }\n" +
                "\n" +
                "            .code {\n" +
                "                font-size: 32px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "    <center> <!-- This ensures it’s centered in email clients -->\n" +
                "        <div class=\"container\">\n" +
                "\n" +
                "            <!-- Header -->\n" +
                "            <div class=\"header\">\n" +
                "                <h1>TaskHive</h1>\n" +
                "                <p>AI-Enhanced Agile Management</p>\n" +
                "            </div>\n" +
                "\n" +
                "            <!-- Content -->\n" +
                "            <div class=\"content\">\n" +
                "                <h2>Welcome, "+ user.getFullName()+" \uD83D\uDC4B</h2>\n" +
                "                <p>Thank you for joining <strong style=\"color:#2563eb;\">TaskHive</strong>! Please use the verification code below to activate your account.</p>\n" +
                "\n" +
                "                <!-- Verification Code Box -->\n" +
                "               <div class=\"code-box\">\n" +
                "    <p class=\"code-title\">Your Verification Code</p>\n" +
                "    <p class=\"code\">"+user.getVerificationCode()+"</p>\n" +
                "    <p class=\"expire\">Expires in 2 minutes.</p>\n" +
                "</div>\n" +
                "                <p class=\"footer-text\">\n" +
                "                    Didn’t request this? Please ignore this email.<br>\n" +
                "                    Need help? <a href=\"#\" style=\"color: #2563eb; text-decoration: underline;\">Contact Support</a>\n" +
                "                </p>\n" +
                "            </div>\n" +
                "\n" +
                "            <!-- Footer -->\n" +
                "            <div class=\"footer\">\n" +
                "                Best Regards,\n" +
                "                <span>TaskHive Team \uD83D\uDC1D</span>\n" +
                "            </div>\n" +
                "\n" +
                "        </div>\n" +
                "    </center>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        emailService.sendEmailToUser(to, subject, htmlText);
    }
}
