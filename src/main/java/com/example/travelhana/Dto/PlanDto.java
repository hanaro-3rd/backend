package com.example.travelhana.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

@Getter @Setter
public class PlanDto {
    Long totalBudget;

    String country;
    String city;
    String title;

    Date startDate;
    Date endDate;

    Long categoryBalance1;
    Long categoryBalance2;
    Long categoryBalance3;
    Long categoryBalance4;
    Long categoryBalance5;
    Long categoryBalance6;
    Long userId;
}

