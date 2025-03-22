package com.example.TaskHive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoriesPostDto
{
    private String title;
    private String description;
    private String storiesPriority;
    private Integer storiesPoint;
}
