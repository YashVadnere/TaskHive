package com.example.TaskHive.repository;

import com.example.TaskHive.entity.Epic;
import com.example.TaskHive.entity.ProductBacklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long>
{
    List<Epic> findAllByProductBacklog(ProductBacklog productBacklog);
}
