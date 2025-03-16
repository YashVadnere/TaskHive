package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_generator"
    )
    @SequenceGenerator(
            name = "user_id_generator",
            sequenceName = "user_id_generator",
            allocationSize = 1
    )
    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String jobTitle;
    @Column(unique = true)
    private String email;
    private String password;
    private Long noOfProjects;
    private Long projectLimit;
    @Enumerated(EnumType.STRING)
    private ActivePlan activePlan;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastLogin;

    private boolean isEnabled;
    private String verificationCode;
    private LocalDateTime verificationCodeExpiresAt;

    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private ProfilePicture profilePicture;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "inviter")
    private List<Invitation> sentInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "invitee")
    private List<Invitation> receivedInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    public List<ProjectMember> projectMembers = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
