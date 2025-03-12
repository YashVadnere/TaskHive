package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "token")
public class Token
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_id_generator"
    )
    @SequenceGenerator(
            name = "token_id_generator",
            sequenceName = "token_id_generator",
            allocationSize = 1
    )
    private Long tokenId;
    private String token;
    private boolean isLoggedOut;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    @JsonBackReference
    private User user;

}
