package com.example.travelhana.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@Builder
public class TravelElementDto {
    String country;
    String city;
    String title;

    Date startDate;
    Date endDate;
    Integer planId;
    Long totalBalance;
    Long totalBudget;

}
