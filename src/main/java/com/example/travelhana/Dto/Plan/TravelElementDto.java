package com.example.travelhana.Dto.Plan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

}
