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
@Table(name = "tasks")
public class Task
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_id_generator"
    )
    @SequenceGenerator(
            name = "task_id_generator",
            sequenceName = "task_id_generator",
            allocationSize =1
    )
    private Long taskId;
    private String title;
    private String description;
    private String taskPriority;
    private Integer taskPoint;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(
            name = "created_by_id",
            referencedColumnName = "userId"
    )
    @JsonBackReference
    private User createdBy;

    @ManyToOne
    @JoinColumn(
            name = "assigned_to_id",
            referencedColumnName = "userId"
    )
    private User assignedTo;

    @ManyToOne
    @JoinColumn(
            name = "stories_id",
            referencedColumnName = "StoriesId"
    )
    @JsonBackReference
    private Stories stories;

}
