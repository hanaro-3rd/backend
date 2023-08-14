package com.example.travelhana.Dto.Plan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CategoryPlanDto {
    int categoryId;
    Long categoryBudget;
    Long categoryBalance;

}
