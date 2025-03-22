package com.example.TaskHive.repository;

import com.example.TaskHive.entity.Epic;
import com.example.TaskHive.entity.Stories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoriesRepository extends JpaRepository<Stories, Long>
{
    List<Stories> findAllByEpic(Epic epic);
}
