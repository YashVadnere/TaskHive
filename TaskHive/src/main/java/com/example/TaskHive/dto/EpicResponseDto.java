package com.example.TaskHive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EpicResponseDto
{
    private Long epicId;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
