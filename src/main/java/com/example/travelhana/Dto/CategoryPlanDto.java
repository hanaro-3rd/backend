package com.example.travelhana.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CategoryPlanDto {
    Integer categoryId;
    Long categoryBudget;
    Long categoryBalance;

}
