package com.example.travelhana.Dto.Plan;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

<<<<<<< HEAD
@Getter
@Setter
public class PlanDto {

	Long totalBudget;

	String country;
	String city;
	String title;

	LocalDateTime startDate;
	LocalDateTime endDate;

	List<CategoryBudgetDto> category;

=======
@Getter @Setter
public class PlanDto {
    Long totalBudget;

    String country;
    String city;
    String title;

    LocalDateTime startDate;
    LocalDateTime endDate;

    List<CategoryBudgetDto> category;
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
}

