package com.example.travelhana.Dto.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCategoryArrayDto {

	List<UpdateCategoryBudgetDto> category;

}
