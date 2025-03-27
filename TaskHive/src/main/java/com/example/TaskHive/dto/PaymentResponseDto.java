package com.example.TaskHive.dto;

import com.example.TaskHive.entity.ActivePlan;
import com.example.TaskHive.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto
{
    private Long paymentId;
    private String name;
    private Long amount;
    private String currency;
    private ActivePlan plan;
    private PaymentStatus paymentStatus;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime paymentDate;
}
