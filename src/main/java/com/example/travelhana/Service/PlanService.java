package com.example.travelhana.Service;

import com.example.travelhana.Dto.Plan.PlanDto;
import com.example.travelhana.Dto.Plan.UpdateCategoryArrayDto;
import com.example.travelhana.Dto.Plan.UpdateTravelBudgetDto;
import org.springframework.http.ResponseEntity;

public interface PlanService {
    ResponseEntity<?> savePlan (String accessToken, PlanDto planDto);
    ResponseEntity<?> getPlanList(String accessToken);
    ResponseEntity<?> getPlan(String accessToken, Integer plan_id);
    ResponseEntity<?> deletePlan(String accessToken, Integer id);
    ResponseEntity<?> updatePlan(String accessToken, UpdateTravelBudgetDto updateTravelBudgetDto);
    ResponseEntity<?> updateCategoryPlan(String accessToken, Integer plan_Id, UpdateCategoryArrayDto updateCategoryArrayDto);
 }
