package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Plan.PlanDto;
import com.example.travelhana.Dto.Plan.UpdateCategoryArrayDto;
import com.example.travelhana.Dto.Plan.UpdatePlanDto;
import com.example.travelhana.Service.PlanService;

import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //api
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

	private final UserService userService;
	private final PlanService planService;

	// 여행 경비 생성
	@PostMapping("")
	public ResponseEntity<?> createPlan(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestBody PlanDto planDto) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.savePlan(users, planDto);
	}

	// 여행 경비 리스트 조회
	@GetMapping(value = "")
	public ResponseEntity<?> getPlanList(
			@RequestHeader(value = "Authorization") String accessToken) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.getPlanList(users);
	}

	// 여행 상세 경비 조회
	@GetMapping(value = "/{planId}")
	public ResponseEntity<?> getPlan(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.getPlan(users, planId);
	}

	@GetMapping(value = "/{planId}/category")
	public ResponseEntity<?> getTravelBudgetByCategory(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.getPlanByCategory(users, planId);
	}

	// 여행 삭제
	@DeleteMapping("/{planId}")
	public ResponseEntity<?> deleteTravelBudget(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.deletePlan(users, planId);
	}

	//여행 제목, 여행지, 여행기간 수정
	@PatchMapping("/{planId}")
	public ResponseEntity<?> updateTravelBudget(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId,
			@RequestBody UpdatePlanDto updatePlanDto) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.updatePlan(users, planId, updatePlanDto);
	}

	//카테고리별 예산 경비 수정
	@PatchMapping("/{planId}/category")
	public ResponseEntity<?> updateTravelCategoryBudget(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int planId,
			@RequestBody UpdateCategoryArrayDto updateCategoryArrayDto) {
		Users users = userService.getUserByAccessToken(accessToken);
		return planService.updateCategoryPlan(users, planId, updateCategoryArrayDto);
	}

}
