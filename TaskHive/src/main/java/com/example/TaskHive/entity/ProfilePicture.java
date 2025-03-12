package com.example.TaskHive.entity;

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
@Table(name = "profile_picture")
public class ProfilePicture
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "profile_picture_id_generator"
    )
    @SequenceGenerator(
            name = "profile_picture_id_generator",
            sequenceName = "profile_picture_id_generator",
            allocationSize = 1
    )
    private Long profilePictureId;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] image;
    private String downloadUrl;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    private User user;

}
