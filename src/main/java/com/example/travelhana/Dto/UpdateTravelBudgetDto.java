package com.example.travelhana.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class UpdateTravelBudgetDto {
    String country;
    String city;
    String title;
    Date startDate;
    Date endDate;
    Long planId;
}
