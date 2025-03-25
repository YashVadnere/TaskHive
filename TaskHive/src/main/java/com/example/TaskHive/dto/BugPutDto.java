package com.example.TaskHive.dto;

import com.example.TaskHive.entity.BugStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BugPutDto
{
    private String title;
    private String description;
    private BugStatus bugStatus;
}
