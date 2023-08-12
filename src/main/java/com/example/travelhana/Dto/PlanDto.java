package com.example.travelhana.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class PlanDto {
    Long totalBudget;

    String country;
    String city;
    String title;

    Date startDate;
    Date endDate;

    List<CategoryBudgetDto> category;
}

