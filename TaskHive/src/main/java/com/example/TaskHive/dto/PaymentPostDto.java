package com.example.TaskHive.dto;

import com.example.TaskHive.entity.ActivePlan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPostDto
{
    private String name;
    private Long amount;
    private String currency;
    private ActivePlan plan;
}
