package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "epics")
public class Epic
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "epic_id_generator"
    )
    @SequenceGenerator(
            name = "epic_id_generator",
            sequenceName = "epic_id_generator",
            allocationSize = 1
    )
    private Long epicId;
    private String title;
    private String description;
    private String priority;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "projectId"
    )
    @JsonBackReference
    private Project project;

    @ManyToOne
    @JoinColumn(
            name = "product_backlog_id",
            referencedColumnName = "productBacklogId"
    )
    @JsonBackReference
    private ProductBacklog productBacklog;

}
