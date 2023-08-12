package com.example.travelhana.Dto.Plan;

import com.example.travelhana.Dto.Plan.UpdateCategoryBudgetDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UpdateCategoryArrayDto {
    List<UpdateCategoryBudgetDto> category;
}
