package com.example.travelhana.Controller;

import com.example.travelhana.Dto.Plan.PlanDto;
import com.example.travelhana.Dto.Plan.UpdateCategoryArrayDto;
<<<<<<< HEAD
import com.example.travelhana.Dto.Plan.UpdatePlanDto;
import com.example.travelhana.Service.PlanService;
=======
import com.example.travelhana.Dto.Plan.UpdateTravelBudgetDto;
import com.example.travelhana.Service.PlanService;
import com.example.travelhana.Service.implement.PlanServiceImpl;
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
@RestController //api
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

	private final PlanService planService;

	// 여행 경비 생성
	@PostMapping("")
	public ResponseEntity<?> createPlan(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestBody PlanDto planDto) {
		return planService.savePlan(accessToken, planDto);
	}

	// 여행 경비 리스트 조회
	@GetMapping(value = "")
	public ResponseEntity<?> getPlanList(
			@RequestHeader(value = "Authorization") String accessToken) {
		return planService.getPlanList(accessToken);
	}

	// 여행 상세 경비 조회
	@GetMapping(value = "/{planId}")
	public ResponseEntity<?> getPlan(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId) {
		return planService.getPlan(accessToken, planId);
	}

	@GetMapping(value = "/{planId}/category")
	public ResponseEntity<?> getTravelBudgetByCategory(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId) {
		return planService.getPlanByCategory(accessToken, planId);
	}

	// 여행 삭제
	@DeleteMapping("/{planId}")
	public ResponseEntity<?> deleteTravelBudget(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId) {
		return planService.deletePlan(accessToken, planId);
	}

	//여행 제목, 여행지, 여행기간 수정
	@PatchMapping("/{planId}")
	public ResponseEntity<?> updateTravelBudget(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId,
			@RequestBody UpdatePlanDto updatePlanDto) {
		return planService.updatePlan(accessToken, planId, updatePlanDto);
	}

	//카테고리별 예산 경비 수정
	@PatchMapping("/{planId}/category")
	public ResponseEntity<?> updateTravelCategoryBudget(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId,
			@RequestBody UpdateCategoryArrayDto updateCategoryArrayDto) {
		return planService.updateCategoryPlan(accessToken, planId, updateCategoryArrayDto);
	}

=======
import java.util.Map;

@RestController //api
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // 여행 경비 생성
    @PostMapping("/travelbudget")
    public ResponseEntity<?> createPlan(@RequestHeader(value = "Authorization") String accessToken, @RequestBody PlanDto planDto) {
        return planService.savePlan(accessToken,planDto);
    }

    // 여행 경비 리스트 조회
    @GetMapping(value = "/travelbudget")
    public ResponseEntity<?> getTravelBudgetList(@RequestHeader(value = "Authorization") String accessToken){
        return planService.getPlanList(accessToken);
    }

    // 여행 상세 경비 조회
    @GetMapping(value = "/travelbudget/{plan_id}")
    public ResponseEntity<?> getTravelBudget(@RequestHeader(value = "Authorization") String accessToken,
                                                               @PathVariable Integer plan_id) {
        return planService.getPlan(accessToken,plan_id);
    }

    @GetMapping(value = "/travelbudget/{plan_id}/category")
    public ResponseEntity<?> getTravelBudgetByCategory (@RequestHeader(value = "Authorization") String accessToken, @PathVariable int plan_id) {
        return planService.getPlanByCategory(accessToken,plan_id);
    }

    // 여행 삭제
    @DeleteMapping("/travelBudget/{plan_id}")
    public ResponseEntity<?> deleteTravelBudget( @RequestHeader(value = "Authorization") String accessToken,@PathVariable int plan_id){
        return planService.deletePlan(accessToken,plan_id);
    }

    //여행 제목, 여행지, 여행기간 수정
    @PatchMapping("/travelBudget")
    public ResponseEntity<?> updateTravelBudget(@RequestHeader(value = "Authorization") String accessToken,@RequestBody UpdateTravelBudgetDto updateTravelBudgetDto) {
        return planService.updatePlan(accessToken,updateTravelBudgetDto);
    }

    //카테고리별 예산 경비 수정
    @PatchMapping("/travelBudget/{plan_id}")
    public ResponseEntity<?> updateTravelCategoryBudget(@RequestHeader(value = "Authorization") String accessToken,@PathVariable int plan_id, @RequestBody UpdateCategoryArrayDto updateCategoryArrayDto) {
        return planService.updateCategoryPlan(accessToken,plan_id,updateCategoryArrayDto);
    }
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
}
