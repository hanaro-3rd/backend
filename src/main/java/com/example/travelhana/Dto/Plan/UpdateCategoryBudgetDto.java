package com.example.travelhana.Dto.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCategoryBudgetDto {

	int categoryId;
	Long categoryBudget;

}
