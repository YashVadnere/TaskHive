package com.example.TaskHive.dto;

import com.example.TaskHive.entity.ProjectStatus;
import com.example.TaskHive.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto
{
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private String projectType;
    private String priority;
    private String visibility;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    private ProjectStatus projectStatus;
    private String createdBy;
    private Role role;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
