package com.example.TaskHive.controller;

import com.example.TaskHive.dto.ProductBacklogDto;
import com.example.TaskHive.service.service_interface.ProductBacklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductBacklogController
{
    private final ProductBacklogService productBacklogService;

    @Autowired
    public ProductBacklogController(ProductBacklogService productBacklogService)
    {
        this.productBacklogService = productBacklogService;
    }

    @GetMapping("products/{projectId}/product-backlogs")
    public ResponseEntity<ProductBacklogDto> getProductBacklogs(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity(productBacklogService.getProductBacklogs(projectId, userDetails), HttpStatus.OK);
    }

}
