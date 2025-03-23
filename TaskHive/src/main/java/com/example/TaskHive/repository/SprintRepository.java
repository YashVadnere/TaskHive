package com.example.TaskHive.repository;

import com.example.TaskHive.entity.Sprint;
import com.example.TaskHive.entity.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long>
{
    List<Sprint> findBySprintStatusAndStartDateBefore(SprintStatus sprintStatus, LocalDateTime localDateTime);

    List<Sprint> findBySprintStatusAndEndDateBefore(SprintStatus sprintStatus, LocalDateTime localDateTime);
}
