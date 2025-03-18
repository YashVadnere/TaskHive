package com.example.TaskHive.repository;

import com.example.TaskHive.entity.ProductBacklog;
import com.example.TaskHive.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductBacklogRepository extends JpaRepository<ProductBacklog, Long>
{
    Optional<ProductBacklog> findByProject(Project project);
}
