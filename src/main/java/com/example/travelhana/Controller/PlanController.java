package com.example.travelhana.Controller;

import com.example.travelhana.Dto.*;
import com.example.travelhana.Service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController //api
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // 여행 경비 생성
    @PostMapping("/travelbudget")
    public PlanSuccessDto createPlan(@RequestBody PlanDto planDto) {
        return planService.savePlan(planDto);
    }

    // 여행 경비 리스트 조회
    @GetMapping(value = "/totalTravelbudget/{id}")
    public ResponseEntity<List<TravelElementDto>> getTravelBudgetList(@PathVariable Long id){
        return planService.getPlanList(id);
    }

    // 여행 상세 경비 조회
    @GetMapping(value = "/travelbudget/{plan_id}")
    public ResponseEntity<Map<String, Object>> getTravelBudget(@PathVariable Long plan_id) {
        return planService.getPlan(plan_id);
    }

    // 여행 삭제
    @DeleteMapping("/travelBudget/{plan_id}")
    public String deleteTravelBudget(@PathVariable Long plan_id){
        return planService.deletePlan(plan_id);
    }

    //여행 제목, 여행지, 여행기간 수정
    @PatchMapping("/travelBudget")
    public String updateTravelBudget(@RequestBody UpdateTravelBudgetDto updateTravelBudgetDto) {
        return planService.updatePlan(updateTravelBudgetDto);
    }

    //카테고리별 예산 경비 수정
    @PatchMapping("/travelBudget/{plan_id}")
    public String updateTravelCategoryBudget(@PathVariable Long plan_id, @RequestBody UpdateCategoryArrayDto updateCategoryArrayDto) {
        return planService.updateCategoryPlan(plan_id,updateCategoryArrayDto);
    }
}
