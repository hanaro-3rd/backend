package com.example.travelhana.Dto.Plan;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class PlanDto {
    Long totalBudget;

    String country;
    String city;
    String title;

    LocalDateTime startDate;
    LocalDateTime endDate;

    List<CategoryBudgetDto> category;
}

