package com.example.TaskHive.dto;

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
public class SprintPostDto
{
    private String sprintName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goal;
    private List<Long> storyId;
}
