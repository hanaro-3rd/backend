package com.example.travelhana.Dto.Plan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TravelElementDto {

	int planId;

	String country;
	String city;
	String title;

	LocalDateTime startDate;
	LocalDateTime endDate;

	Long totalBalance;
	Long totalBudget;

}
