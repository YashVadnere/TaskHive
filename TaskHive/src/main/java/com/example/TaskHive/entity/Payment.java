package com.example.TaskHive.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payment_id_generator"
    )
    @SequenceGenerator(
            name = "payment_id_generator",
            sequenceName = "payment_id_generator",
            allocationSize = 1
    )
    private Long paymentId;
    private String name;
    private Long quantity;
    private Long amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private ActivePlan plan;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    @JsonBackReference
    private User user;

}
