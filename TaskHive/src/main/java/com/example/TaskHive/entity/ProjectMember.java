package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_member")
public class ProjectMember
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_member_id_generator"
    )
    @SequenceGenerator(
            name = "project_member_id_generator",
            sequenceName = "project_member_id_generator",
            allocationSize = 1
    )
    private Long projectMemberId;
    @Enumerated(EnumType.STRING)
    private Role role;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    public User user;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "projectId"
    )
    private Project project;

}
