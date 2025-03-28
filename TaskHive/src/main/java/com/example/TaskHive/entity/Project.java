package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_id_generator"
    )
    @SequenceGenerator(
            name = "project_id_generator",
            sequenceName = "project_id_generator",
            allocationSize = 1
    )
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private String projectType;
    private String priority;
    private String visibility;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    @JsonIgnore
    private User user;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Invitation> invitations = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToOne(
            mappedBy = "project",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private ProductBacklog productBacklog;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Epic> epics = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Sprint> sprints;

}
