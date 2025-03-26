package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.SprintGetDto;
import com.example.TaskHive.dto.SprintPostDto;
import com.example.TaskHive.dto.SprintPutDto;
import com.example.TaskHive.entity.*;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.ProjectRepository;
import com.example.TaskHive.repository.SprintRepository;
import com.example.TaskHive.repository.StoriesRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.SprintService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SprintServiceImplementation implements SprintService
{
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final StoriesRepository storiesRepository;

    @Autowired
    public SprintServiceImplementation(
            SprintRepository sprintRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            StoriesRepository storiesRepository
    ) {
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.storiesRepository = storiesRepository;
    }

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void updateSprintStatusByScheduler()
    {
        LocalDateTime now = LocalDateTime.now();

        List<Sprint> plannedSprints = sprintRepository.findBySprintStatusAndStartDateBefore(SprintStatus.PLANNED, now);

        for (Sprint sprint: plannedSprints)
        {
            sprint.setSprintStatus(SprintStatus.ACTIVE);
        }
        sprintRepository.saveAll(plannedSprints);

        List<Sprint> activeSprints = sprintRepository.findBySprintStatusAndEndDateBefore(SprintStatus.ACTIVE, now);

        for (Sprint sprint: activeSprints)
        {
            sprint.setSprintStatus(SprintStatus.COMPLETED);
        }
        sprintRepository.saveAll(activeSprints);

        List<User> expiredUsers = userRepository.findAllByPlanEndsAtBefore(now);

        for (User user : expiredUsers)
        {
            if(user.getActivePlan() != ActivePlan.FREE)
            {
                user.setActivePlan(ActivePlan.FREE);
                user.setProjectLimit(3L);
                user.setPlanEndsAt(null);
                userRepository.save(user);
            }
        }
    }

    @Override
    @Transactional
    public ResponseDto create(Long projectId, UserDetails userDetails, SprintPostDto dto)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("project does not belong to the user");
        }

        Sprint sprint = mapSprintPostDtoToSprintEntity(dto);
        sprint.setSprintStatus(SprintStatus.PLANNED);
        sprint.setProject(project);
        project.getSprints().add(sprint);

        if(dto.getStoryId()!=null && !dto.getStoryId().isEmpty())
        {
            List<Stories> stories = storiesRepository.findAllById(dto.getStoryId());
            sprint.setStories(stories);
            stories.forEach(stories1 -> stories1.setSprint(sprint));
            storiesRepository.saveAll(stories);
        }

        sprintRepository.save(sprint);
        projectRepository.save(project);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Sprint created successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public List<SprintGetDto> getAllSprints(Long projectId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        return project.getSprints().stream()
                .map(this::mapSprintEntityToSprintGetDto)
                .toList();

    }

    @Override
    @Transactional
    public SprintGetDto getSprintById(Long projectId, Long sprintId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFound("Sprint not found"));

        return mapSprintEntityToSprintGetDto(sprint);
    }

    @Override
    @Transactional
    public ResponseDto update(Long projectId, Long sprintId, SprintPutDto dto, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFound("Sprint not found"));

        if(StringUtils.hasText(dto.getSprintName()))
        {
            sprint.setSprintName(dto.getSprintName());
        }
        if(dto.getStartDate()!=null)
        {
            sprint.setStartDate(dto.getStartDate());
        }
        if(dto.getEndDate()!=null)
        {
            sprint.setEndDate(dto.getEndDate());
        }
        if(StringUtils.hasText(dto.getGoal()))
        {
            sprint.setGoal(dto.getGoal());
        }
        if(dto.getStoryId()!=null && !dto.getStoryId().isEmpty())
        {
            List<Stories> stories = storiesRepository.findAllById(dto.getStoryId());
            sprint.setStories(stories);
            stories.forEach(stories1 -> stories1.setSprint(sprint));
            storiesRepository.saveAll(stories);
        }

        sprintRepository.save(sprint);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Sprint updated successfully");
        return responseDto;
    }

    @Override
    @Transactional
    public ResponseDto delete(Long projectId, Long sprintId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));

        if(!project.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Project does not belong to user");
        }

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFound("Sprint not found"));

        sprint.getStories().forEach(stories -> stories.setSprint(null));
        storiesRepository.saveAll(sprint.getStories());
        sprintRepository.delete(sprint);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Sprint deleted successfully");
        return responseDto;
    }

    private SprintGetDto mapSprintEntityToSprintGetDto(Sprint sprint)
    {
        SprintGetDto dto = new SprintGetDto();
        dto.setSprintId(sprint.getSprintId());
        dto.setSprintName(sprint.getSprintName());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setGoal(sprint.getGoal());
        dto.setSprintStatus(sprint.getSprintStatus());
        dto.setProjectId(sprint.getProject().getProjectId());

        List<Long> storiesId = sprint.getStories().stream()
                .map(Stories::getStoriesId)
                .toList();

        dto.setStoriesId(storiesId);
        return dto;
    }

    private Sprint mapSprintPostDtoToSprintEntity(SprintPostDto dto)
    {
        Sprint sprint = new Sprint();
        sprint.setSprintName(dto.getSprintName());
        sprint.setStartDate(dto.getStartDate());
        sprint.setEndDate(dto.getEndDate());
        sprint.setGoal(dto.getGoal());
        return sprint;
    }

}
