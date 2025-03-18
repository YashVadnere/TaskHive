package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.ProductBacklogDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductBacklogService
{

    ProductBacklogDto getProductBacklogs(Long projectId, UserDetails userDetails);
}
