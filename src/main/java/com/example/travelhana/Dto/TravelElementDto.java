package com.example.travelhana.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class TravelElementDto {
    String country;
    String city;
    String title;

    Date startDate;
    Date endDate;
    Long planId;
    Long totalBalance;
    Long totalBudget;

}
