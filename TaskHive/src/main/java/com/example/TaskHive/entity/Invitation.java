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
@Table(name = "invitation")
public class Invitation
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "invitation_id_generator"
    )
    @SequenceGenerator(
            name = "invitation_id_generator",
            sequenceName = "invitation_id_generator",
            allocationSize = 1
    )
    private Long invitationId;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime respondedAt;

    @ManyToOne
    @JoinColumn(
            name = "inviter_id",
            referencedColumnName = "userId"
    )
    private User inviter;

    @ManyToOne
    @JoinColumn(
            name = "invitee_id",
            referencedColumnName = "userId"
    )
    private User invitee;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "projectId"
    )
    private Project project;

}
