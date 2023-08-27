package com.example.travelhana.Dto.Plan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UpdatePlanDto {

	String country;
	String city;
	String title;
	LocalDateTime startDate;
	LocalDateTime endDate;

}
