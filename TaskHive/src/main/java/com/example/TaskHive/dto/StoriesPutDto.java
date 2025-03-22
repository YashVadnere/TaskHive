package com.example.TaskHive.dto;

import com.example.TaskHive.entity.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoriesPutDto
{
    private String title;
    private String description;
    private String storiesPriority;
    private Integer storiesPoint;
    @Enumerated(EnumType.STRING)
    private Status storiesStatus;
}
