package com.example.travelhana.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UpdateCategoryArrayDto {
    List<UpdateCategoryBudgetDto> category;
}
