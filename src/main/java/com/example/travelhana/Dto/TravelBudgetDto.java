package com.example.travelhana.Dto;



import com.example.travelhana.Domain.CategoryPlan;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter @Setter
@Builder
public class TravelBudgetDto {
    String country;
    String city;
    String title;

    Date startDate;
    Date endDate;

}
