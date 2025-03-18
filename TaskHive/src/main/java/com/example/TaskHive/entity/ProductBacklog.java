package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_backlog")
public class ProductBacklog
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_backlog_id_generator"
    )
    @SequenceGenerator(
            name = "product_backlog_id_generator",
            sequenceName = "product_backlog_id_generator",
            allocationSize = 1
    )
    private Long productBacklogId;
    @Enumerated(EnumType.STRING)
    private ProductBacklogStatus productBacklogStatus;

    @OneToOne
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "projectId"
    )
    private Project project;

    @OneToMany(mappedBy = "productBacklog")
    @JsonManagedReference
    private List<Epic> epics = new ArrayList<>();

}
