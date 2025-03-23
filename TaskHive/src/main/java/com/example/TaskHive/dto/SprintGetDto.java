package com.example.TaskHive.dto;

import com.example.TaskHive.entity.SprintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SprintGetDto
{
    private Long sprintId;
    private String sprintName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goal;
    private SprintStatus sprintStatus;
    private Long projectId;
    private List<Long> storiesId;

}
