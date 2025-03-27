package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.EpicDto;
import com.example.TaskHive.dto.EpicResponseDto;
import com.example.TaskHive.dto.EpicUpdateDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.Epic;
import com.example.TaskHive.entity.ProductBacklog;
import com.example.TaskHive.entity.Project;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.EpicRepository;
import com.example.TaskHive.repository.ProductBacklogRepository;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.EpicService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EpicServiceImplementation implements EpicService
{
    private final EpicRepository epicRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProductBacklogRepository productBacklogRepository;

    @Autowired
    public EpicServiceImplementation(
            EpicRepository epicRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProductBacklogRepository productBacklogRepository
    ) {
        this.epicRepository = epicRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.productBacklogRepository = productBacklogRepository;
    }


    @Override
    @Transactional
    public ResponseDto createEpic(Long projectId, UserDetails userDetails, EpicDto dto)
    {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        ProductBacklog productBacklog = productBacklogRepository.findByProject(project)
                .orElseThrow(() -> new ResourceNotFound("Product backlog not found"));

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Epic epic = mapEpicDtoToEpicEntity(dto);
        epic.setCreatedAt(LocalDateTime.now());
        epic.setUpdatedAt(LocalDateTime.now());
        epic.setUser(user);
        epic.setProject(project);
        epic.setProductBacklog(productBacklog);

        user.getEpics().add(epic);
        project.getEpics().add(epic);
        productBacklog.getEpics().add(epic);

        epicRepository.save(epic);
        userRepository.save(user);
        projectRepository.save(project);
        productBacklogRepository.save(productBacklog);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Epic created successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public List<EpicResponseDto> getAllEpicsByProjectId(Long projectId, UserDetails userDetails)
    {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        ProductBacklog productBacklog = productBacklogRepository.findByProject(project)
                .orElseThrow(() -> new ResourceNotFound("Product backlog not found"));

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        List<Epic> epics = epicRepository.findAllByProductBacklog(productBacklog);
        return epics.stream()
                .map(this::mapEpicEntityToEpicResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ResponseDto updateEpic(Long projectId, Long epicId, EpicUpdateDto dto, UserDetails userDetails)
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

        Epic epic = productBacklog.getEpics()
                .stream()
                .filter(epic1 -> epic1.getEpicId().equals(epicId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        if (StringUtils.hasText(dto.getTitle())) {
            epic.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            epic.setDescription(dto.getDescription());
        }
        if (StringUtils.hasText(dto.getPriority())) {
            epic.setPriority(dto.getPriority());
        }
        epic.setUpdatedAt(LocalDateTime.now());
        epicRepository.save(epic);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Epic updated successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public ResponseDto deleteEpic(Long projectId, Long epicId, UserDetails userDetails)
    {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        ProductBacklog productBacklog = productBacklogRepository.findByProject(project)
                .orElseThrow(() -> new ResourceNotFound("Product backlog not found"));

        Epic epic = productBacklog.getEpics()
                .stream()
                .filter(epic1 -> epic1.getEpicId().equals(epicId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        epicRepository.delete(epic);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Epic deleted successfully");
        return responseDto;
    }

    @Override
    public EpicResponseDto getEpicsById(Long projectId, Long epicId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found"));

        return mapEpicEntityToEpicResponseDto(epic);
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

    private Epic mapEpicDtoToEpicEntity(EpicDto dto)
    {
        Epic epic = new Epic();
        epic.setTitle(dto.getTitle());
        epic.setDescription(dto.getDescription());
        epic.setPriority(dto.getPriority());
        return epic;
    }

}
