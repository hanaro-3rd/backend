package com.example.travelhana.Dto;



import com.example.travelhana.Domain.CategoryPlan;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter @Setter
public class TravelBudgetDto {
    String country;
    String city;
    String title;

    Date startDate;
    Date endDate;

}
