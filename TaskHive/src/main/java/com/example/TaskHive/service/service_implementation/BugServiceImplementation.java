package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.BugGetDto;
import com.example.TaskHive.dto.BugPostDto;
import com.example.TaskHive.dto.BugPutDto;
import com.example.TaskHive.dto.ResponseDto;
import com.example.TaskHive.entity.Bug;
import com.example.TaskHive.entity.Task;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.BugRepository;
import com.example.TaskHive.repository.TaskRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BugServiceImplementation implements BugService
{
    private final BugRepository bugRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public BugServiceImplementation(
            BugRepository bugRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.bugRepository = bugRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDto create(Long taskId, BugPostDto dto, UserDetails userDetails)
    {
        User creator = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("Creator not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        if(!task.getStories().getEpic().getUser().getUserId().equals(creator.getUserId()))
        {
            throw new Mismatch("Task does not belong to user");
        }

        Bug bug = mapBugPostDtoToBugEntity(dto);
        bug.setCreatedAt(LocalDateTime.now());
        bug.setUpdatedAt(LocalDateTime.now());
        bug.setTask(task);
        bug.setCreator(creator);

        creator.getBugs().add(bug);
        task.getBugs().add(bug);

        bugRepository.save(bug);
        taskRepository.save(task);
        userRepository.save(creator);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Bug created successfully");
        return responseDto;
    }

    @Override
    public List<BugGetDto> getAllBugs(Long taskId, UserDetails userDetails)
    {
        User creator = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("Creator not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        if(!task.getStories().getEpic().getUser().getUserId().equals(creator.getUserId()))
        {
            throw new Mismatch("Task does not belong to user");
        }

        return task.getBugs()
                .stream()
                .map(this::mapBugEntityToBugGetDto)
                .toList();
    }

    @Override
    public BugGetDto getBugById(Long taskId, Long bugId, UserDetails userDetails)
    {
        User creator = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("Creator not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        if(!task.getStories().getEpic().getUser().getUserId().equals(creator.getUserId()))
        {
            throw new Mismatch("Task does not belong to user");
        }

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found"));

        return mapBugEntityToBugGetDto(bug);
    }

    @Override
    public ResponseDto update(Long taskId, Long bugId, BugPutDto dto, UserDetails userDetails)
    {
        User creator = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("Creator not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        if(!task.getStories().getEpic().getUser().getUserId().equals(creator.getUserId()))
        {
            throw new Mismatch("Task does not belong to user");
        }

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found"));

        if(StringUtils.hasText(dto.getTitle()))
        {
            bug.setTitle(dto.getTitle());
        }
        if(StringUtils.hasText(dto.getDescription()))
        {
            bug.setDescription(dto.getDescription());
        }
        if(dto.getBugStatus()!=null)
        {
            bug.setBugStatus(dto.getBugStatus());
        }

        bugRepository.save(bug);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Bug updated successfully");
        return responseDto;
    }

    @Override
    public ResponseDto delete(Long taskId, Long bugId, UserDetails userDetails)
    {
        User creator = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("Creator not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        if(!task.getStories().getEpic().getUser().getUserId().equals(creator.getUserId()))
        {
            throw new Mismatch("Task does not belong to user");
        }

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found"));

        bugRepository.delete(bug);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Bug deleted successfully");
        return responseDto;
    }

    private BugGetDto mapBugEntityToBugGetDto(Bug bug)
    {
        BugGetDto dto = new BugGetDto();
        dto.setBugId(bug.getBugId());
        dto.setTitle(bug.getTitle());
        dto.setDescription(bug.getDescription());
        dto.setBugStatus(bug.getBugStatus());
        dto.setCreatedAt(bug.getCreatedAt());
        dto.setUpdatedAt(bug.getUpdatedAt());
        dto.setTaskId(bug.getTask().getTaskId());
        dto.setTaskTitle(bug.getTask().getTitle());
        dto.setCreatorId(bug.getCreator().getUserId());
        dto.setCreatorName(bug.getCreator().getFullName());
        return dto;
    }

    private Bug mapBugPostDtoToBugEntity(BugPostDto dto)
    {
        Bug bug = new Bug();
        bug.setTitle(dto.getTitle());
        bug.setDescription(dto.getDescription());
        bug.setBugStatus(dto.getBugStatus());
        return bug;
    }
}
