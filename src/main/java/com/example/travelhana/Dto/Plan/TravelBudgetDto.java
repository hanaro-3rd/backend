package com.example.travelhana.Dto.Plan;


<<<<<<< HEAD
=======

import com.example.travelhana.Domain.CategoryPlan;
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
<<<<<<< HEAD

@Getter
@Setter
@Builder
public class TravelBudgetDto {

	String country;
	String city;
	String title;

	LocalDateTime startDate;
	LocalDateTime endDate;
=======
import java.util.Date;
import java.util.List;

@Getter @Setter
@Builder
public class TravelBudgetDto {
    String country;
    String city;
    String title;

    LocalDateTime startDate;
    LocalDateTime endDate;
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3

}
