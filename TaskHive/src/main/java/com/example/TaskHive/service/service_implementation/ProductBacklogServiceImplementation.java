package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.EpicResponseDto;
import com.example.TaskHive.dto.ProductBacklogDto;
import com.example.TaskHive.entity.Epic;
import com.example.TaskHive.entity.ProductBacklog;
import com.example.TaskHive.entity.Project;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProductBacklogRepository;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.ProductBacklogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ProductBacklogServiceImplementation implements ProductBacklogService
{
    private final ProductBacklogRepository productBacklogRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductBacklogServiceImplementation(
            ProductBacklogRepository productBacklogRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.productBacklogRepository = productBacklogRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ProductBacklogDto getProductBacklogs(Long projectId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        ProductBacklog productBacklog = productBacklogRepository.findByProject(project)
                .orElseThrow(() -> new ResourceNotFound("Product backlog not found"));

        ProductBacklogDto dto = new ProductBacklogDto();
        dto.setProductBacklogId(productBacklog.getProductBacklogId());
        dto.setEpicResponseDtos(
                productBacklog.getEpics().stream()
                        .map(this::mapEpicEntityToEpicResponseDto)
                        .toList()
        );
        return dto;
    }

    private EpicResponseDto mapEpicEntityToEpicResponseDto(Epic epic)
    {
        EpicResponseDto dto = new EpicResponseDto();
        dto.setEpicId(epic.getEpicId());
        dto.setTitle(epic.getTitle());
        dto.setDescription(epic.getDescription());
        dto.setPriority(epic.getPriority());
        dto.setCreatedAt(epic.getCreatedAt());
        dto.setUpdatedAt(epic.getUpdatedAt());
        return dto;
    }

}
