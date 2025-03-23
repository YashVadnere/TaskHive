package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stories")
public class Stories
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "stories_id_generator"
    )
    @SequenceGenerator(
            name = "stories_id_generator",
            sequenceName = "stories_id_generator",
            allocationSize = 1
    )
    private Long storiesId;
    private String title;
    private String description;
    private String storiesPriority;
    private Integer storiesPoint;
    @Enumerated(EnumType.STRING)
    private Status storiesStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "epic_id",
            referencedColumnName = "epicId"
    )
    @JsonBackReference
    private Epic epic;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "stories", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(
            name = "sprint_id",
            referencedColumnName = "sprintId"
    )
    @JsonBackReference
    private Sprint sprint;

}
