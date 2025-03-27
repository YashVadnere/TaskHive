package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sprints")
public class Sprint
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sprint_id_generator"
    )
    @SequenceGenerator(
            name = "sprint_id_generator",
            sequenceName = "sprint_id_generator",
            allocationSize = 1
    )
    private Long sprintId;
    private String sprintName;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;
    private String goal;
    @Enumerated(EnumType.STRING)
    private SprintStatus sprintStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "projectId"
    )
    @JsonBackReference
    private Project project;

    @OneToMany(mappedBy = "sprint")
    @JsonManagedReference
    private List<Stories> stories;
}
