package com.example.TaskHive.config;

import com.example.TaskHive.entity.ActivePlan;
import com.example.TaskHive.entity.ProfilePicture;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.repository.ProfilePictureRepository;
import com.example.TaskHive.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Transactional
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
    private final UserRepository userRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Value("${spring.app.oauth2.redirect-url}")
    private String redirectUrl;

    @Autowired
    public OAuthAuthenticationSuccessHandler(
            UserRepository userRepository,
            ProfilePictureRepository profilePictureRepository,
            JwtAuthenticationService jwtAuthenticationService
    ) {
        this.userRepository = userRepository;
        this.profilePictureRepository = profilePictureRepository;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

        System.out.println(user.getName());
        user.getAttributes().forEach((key,value) -> System.out.println(key+" "+value));

        String email = user.getAttribute("email");
        String firstname = user.getAttribute("given_name");
        String lastname = user.getAttribute("family_name");
        String downloadUrl = user.getAttribute("picture");
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent())
        {
            User user1 = optionalUser.get();
            user1.setLastLogin(LocalDateTime.now());
            userRepository.save(user1);
        }
        else
        {
            User newUser = new User();
            newUser.setFirstName(firstname);
            newUser.setLastName(lastname);
            newUser.setJobTitle(null);
            newUser.setEmail(email);
            newUser.setPassword(null);
            newUser.setNoOfProjects(0L);
            newUser.setActivePlan(ActivePlan.FREE);
            newUser.setLastLogin(LocalDateTime.now());

            newUser.setEnabled(true);
            newUser.setVerificationCode(null);
            newUser.setVerificationCodeExpiresAt(null);

            ProfilePicture profilePicture = new ProfilePicture();
            profilePicture.setFileName(null);
            profilePicture.setFileType(null);
            profilePicture.setImage(null);
            profilePicture.setDownloadUrl(downloadUrl);

            newUser.setProfilePicture(profilePicture);
            profilePicture.setUser(newUser);

            userRepository.save(newUser);
            profilePictureRepository.save(profilePicture);



        }
        response.sendRedirect(redirectUrl);

    }
}
