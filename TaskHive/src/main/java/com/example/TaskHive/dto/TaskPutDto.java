package com.example.TaskHive.dto;

import com.example.TaskHive.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskPutDto
{
    private String title;
    private String description;
    private Status taskStatus;
    private String taskPriority;
    private Integer taskPoint;
    private Long assignedToUserId;
}
