package com.example.travelhana.Service;

import com.example.travelhana.Dto.Plan.PlanDto;
import com.example.travelhana.Dto.Plan.UpdateCategoryArrayDto;
import com.example.travelhana.Dto.Plan.UpdatePlanDto;
import org.springframework.http.ResponseEntity;

public interface PlanService {

    ResponseEntity<?> savePlan (String accessToken, PlanDto planDto);

    ResponseEntity<?> getPlanList(String accessToken);

    ResponseEntity<?> getPlan(String accessToken, int planId);

    ResponseEntity<?> getPlanByCategory(String accessToken, int planId);

    ResponseEntity<?> deletePlan(String accessToken, int planId);

    ResponseEntity<?> updatePlan(String accessToken, int planId, UpdatePlanDto updatePlanDto);

    ResponseEntity<?> updateCategoryPlan(String accessToken, int planId, UpdateCategoryArrayDto updateCategoryArrayDto);
    void makeCategory();
}

