package com.example.travelhana.Dto.Plan;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdatePlanDto {

	String country;
	String city;
	String title;
	LocalDateTime startDate;
	LocalDateTime endDate;

}
