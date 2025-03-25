package com.example.TaskHive.dto;

import com.example.TaskHive.entity.BugStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BugGetDto
{
    private Long bugId;
    private String title;
    private String description;
    private BugStatus bugStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long taskId;
    private String taskTitle;
    private Long creatorId;
    private String creatorName;
}
