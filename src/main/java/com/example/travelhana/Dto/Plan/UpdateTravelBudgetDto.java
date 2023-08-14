package com.example.travelhana.Dto.Plan;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter
public class UpdateTravelBudgetDto {
    String country;
    String city;
    String title;
    LocalDateTime startDate;
    LocalDateTime endDate;
    int planId;
}
