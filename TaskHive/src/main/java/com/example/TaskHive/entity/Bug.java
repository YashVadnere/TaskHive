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
@Table(name = "bugs")
public class Bug
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bug_id_generator"
    )
    @SequenceGenerator(
            name = "bug_id_generator",
            sequenceName = "bug_id_generator",
            allocationSize = 1
    )
    private Long bugId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private BugStatus bugStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "task_id",
            referencedColumnName = "taskId"
    )
    @JsonBackReference
    private Task task;

    @ManyToOne
    @JoinColumn(
            name = "creator_id",
            referencedColumnName = "userId"
    )
    @JsonBackReference
    private User creator;

}