package com.example.TaskHive.repository;

import com.example.TaskHive.entity.ProjectMember;
import com.example.TaskHive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long>
{
    List<ProjectMember> findAllByUser(User user);
}
