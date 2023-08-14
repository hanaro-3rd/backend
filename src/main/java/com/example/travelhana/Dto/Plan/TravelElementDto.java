package com.example.travelhana.Dto.Plan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
<<<<<<< HEAD

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
=======
import java.util.Date;

@Getter @Setter
@Builder
public class TravelElementDto {
    String country;
    String city;
    String title;

    LocalDateTime startDate;
    LocalDateTime endDate;
    int planId;
    Long totalBalance;
    Long totalBudget;
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3

}
