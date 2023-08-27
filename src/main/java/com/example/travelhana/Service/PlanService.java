package com.example.travelhana.Service;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Plan.PlanDto;
import com.example.travelhana.Dto.Plan.UpdateCategoryArrayDto;
import com.example.travelhana.Dto.Plan.UpdatePlanDto;
import org.springframework.http.ResponseEntity;

public interface PlanService {

    ResponseEntity<?> savePlan (Users users, PlanDto planDto);

    ResponseEntity<?> getPlanList(Users users);

    ResponseEntity<?> getPlan(Users users, int planId);

    ResponseEntity<?> getPlanByCategory(Users users, int planId);

    ResponseEntity<?> deletePlan(Users users, int planId);

    ResponseEntity<?> updatePlan(Users users, int planId, UpdatePlanDto updatePlanDto);

    ResponseEntity<?> updateCategoryPlan(Users users, int planId, UpdateCategoryArrayDto updateCategoryArrayDto);

}

