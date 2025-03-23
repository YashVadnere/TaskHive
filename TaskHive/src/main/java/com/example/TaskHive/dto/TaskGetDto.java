package com.example.TaskHive.dto;

import com.example.TaskHive.entity.Status;
import com.example.TaskHive.entity.Stories;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskGetDto
{
    private Long taskId;
    private String title;
    private String description;
    private String taskPriority;
    private Integer taskPoint;
    private Status status;
    private String createdBy;
    private String assignedTo;
    private Long storiesId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
