package com.example.TaskHive.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateDto
{
    private String projectName;
    private String projectDescription;
    private String projectType;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    private String priority;
    private String visibility;
}
