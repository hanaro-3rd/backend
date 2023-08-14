package com.example.travelhana.Dto.Plan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PlanSuccessDto {

    int planId;
    int userId;

}
