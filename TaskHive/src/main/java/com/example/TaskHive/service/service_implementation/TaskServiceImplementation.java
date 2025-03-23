package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.dto.TaskGetDto;
import com.example.TaskHive.dto.TaskPostDto;
import com.example.TaskHive.dto.TaskPutDto;
import com.example.TaskHive.entity.Stories;
import com.example.TaskHive.entity.Task;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.StoriesRepository;
import com.example.TaskHive.repository.TaskRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImplementation implements TaskService
{
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final StoriesRepository storiesRepository;


    @Autowired
    public TaskServiceImplementation(
            TaskRepository taskRepository,
            UserRepository userRepository,
            StoriesRepository storiesRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.storiesRepository = storiesRepository;
    }

    @Override
    public ResponseDto create(Long storiesId, TaskPostDto dto, UserDetails userDetails)
    {
        User creator = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        if(!stories.getEpic().getUser().getUserId().equals(creator.getUserId()))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        Task task = mapTaskPostDtoToTaskEntity(dto);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setCreatedBy(creator);
        task.setStories(stories);
        taskRepository.save(task);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Task created successfully");
        return responseDto;
    }

    @Override
    public List<TaskGetDto> getAllTasks(Long storiesId, UserDetails userDetails)
    {
        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        return stories.getTasks()
                .stream()
                .map(task -> mapTaskEntityToTaskGetDto(task, stories))
                .toList();
    }

    @Override
    public TaskGetDto getTaskById(Long storiesId, Long taskId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        if(!stories.getEpic().getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        return mapTaskEntityToTaskGetDto(task, stories);

    }

    @Override
    public ResponseDto update(Long storiesId, Long taskId, TaskPutDto dto, UserDetails userDetails)
    {
        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        if(!stories.getEpic().getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        if(StringUtils.hasText(dto.getTitle()))
        {
            task.setTitle(dto.getTitle());
        }
        if(StringUtils.hasText(dto.getDescription()))
        {
            task.setDescription(dto.getDescription());
        }
        if(dto.getTaskStatus()!=null)
        {
            task.setStatus(dto.getTaskStatus());
        }
        if(StringUtils.hasText(dto.getTaskPriority()))
        {
            task.setTaskPriority(dto.getTaskPriority());
        }
        if(dto.getTaskPoint()!=null)
        {
            task.setTaskPoint(dto.getTaskPoint());
        }
        if(dto.getAssignedToUserId()!=null)
        {
            User assignedTo = userRepository.findById(dto.getAssignedToUserId())
                            .orElseThrow(() -> new ResourceNotFound("Assigned user not found"));
            task.setAssignedTo(assignedTo);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Task updated successfully");
        return responseDto;
    }

    @Override
    public ResponseDto deleteTaskById(Long storiesId, Long taskId, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Stories stories = storiesRepository.findById(storiesId)
                .orElseThrow(() -> new ResourceNotFound("Story not found"));


        if(!stories.getEpic().getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Story does not belong to epic");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        taskRepository.delete(task);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Task deleted successfully");
        return responseDto;
    }

    private TaskGetDto mapTaskEntityToTaskGetDto(Task task, Stories stories)
    {
        TaskGetDto dto = new TaskGetDto();
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTaskPriority(task.getTaskPriority());
        dto.setTaskPoint(task.getTaskPoint());
        dto.setStatus(task.getStatus());
        dto.setCreatedBy(task.getCreatedBy().getFullName());

        if(task.getAssignedTo()!=null) {
            dto.setAssignedTo(task.getAssignedTo().getFullName());
        } else {
            dto.setAssignedTo("Unassigned");
        }

        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setStoriesId(stories.getStoriesId());
        return dto;
    }

    private Task mapTaskPostDtoToTaskEntity(TaskPostDto dto)
    {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskPriority(dto.getTaskPriority());
        task.setTaskPoint(dto.getTaskPoint());
        task.setStatus(dto.getTaskStatus());

        User assignedTo = userRepository.findById(dto.getAssignedToUserId())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        task.setAssignedTo(assignedTo);
        return task;
    }


}
