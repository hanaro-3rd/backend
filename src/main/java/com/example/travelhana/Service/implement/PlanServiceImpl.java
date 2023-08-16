package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Plan.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.*;
import com.example.travelhana.Service.PlanService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

	private final UserService userService;

	private final PlanRepository planRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryPlanRepository categoryPlanRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final KeymoneyRepository keymoneyRepository;

	//경비 생성
	public ResponseEntity<?> savePlan(String accessToken, PlanDto planDto) {
		try {
			// accessToken으로 유저 검증
			User userAccount = userService.getUserByAccessToken(accessToken);

			// 경비 계획 저장
			Plan plan = Plan.builder()
					.title(planDto.getTitle())
					.city(planDto.getCity())
					.startDate(planDto.getStartDate())
					.endDate(planDto.getEndDate())
					.country(planDto.getCountry())
					.totalBalance(planDto.getTotalBudget())
					.totalBudget(planDto.getTotalBudget()).user(userAccount).build();
			Plan returnPlan = planRepository.save(plan);

			// 경비 계획에 필요한 카테고리명, 카테고리 경비, 카테고리 잔액 저장
			List<CategoryPlan> categoryPlanList = new ArrayList<>();
			List<CategoryBudgetDto> updateCategoryBudgetDtoList = planDto.getCategory();
			for (CategoryBudgetDto updateCategoryBudgetDto : updateCategoryBudgetDtoList) {
				CategoryPlan categoryPlan = CategoryPlan.builder()
						.category(categoryRepository.findById(updateCategoryBudgetDto.getCategoryId())
								.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND)))
						.plan(returnPlan)
						.categoryBalance(updateCategoryBudgetDto.getCategoryBudget())
						.categoryBudget(updateCategoryBudgetDto.getCategoryBudget())
						.build();
				categoryPlanList.add(categoryPlan);
			}
			categoryPlanRepository.saveAll(categoryPlanList);

			// 성공시 userId와 생성한 planId
			PlanSuccessDto planSuccessDto = PlanSuccessDto
					.builder()
					.userId(userAccount.getId())
					.planId(returnPlan.getId())
					.build();

			// apiResponse에 담아서 리턴
			ApiResponse apiResponse = ApiResponse.builder()
					.result(planSuccessDto)
					.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(e.getMessage())
					.errorCode(e.getErrorCode().getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 경비계획 리스트 읽기
	public ResponseEntity<?> getPlanList(String accessToken) {
		try {
			// accessToken으로 유저 검증
			User userAccount = userService.getUserByAccessToken(accessToken);

			// 유저가 가지고 있는 경비계획 리스트 가져오기
			List<Plan> planList = planRepository.findAllByUser_IdAndIsDeletedFalse(userAccount.getId());

			// 가져온 엔티티를 dto에 파싱
			List<TravelElementDto> travelElementDtoList = new ArrayList<>();
			for (Plan plan : planList) {
				TravelElementDto travelElementDto = TravelElementDto
						.builder()
						.planId((plan.getId()))
						.title(plan.getTitle())
						.country(plan.getCountry())
						.startDate(plan.getStartDate())
						.endDate(plan.getEndDate())
						.city(plan.getCity())
						.totalBalance(plan.getTotalBalance())
						.totalBudget(plan.getTotalBudget())
						.build();
				travelElementDtoList.add(travelElementDto);
			}

			// apiResponse에 담아서 리턴
			ApiResponse apiResponse = ApiResponse.builder()
					.result(travelElementDtoList)
					.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
					.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
					.build();
			return new ResponseEntity<>(apiResponse, HttpStatus.OK);
		} catch (BusinessExceptionHandler e) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.errorMessage(e.getMessage())
					.errorCode(e.getErrorCode().getStatusCode())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 경비 계획 상세 읽어오기
	public ResponseEntity<?> getPlan(String accessToken, int planId) {
		// accessToken으로 유저 검증
		User userAccount = userService.getUserByAccessToken(accessToken);

		// planId와 userId로 유저 소유 여부 식별 및 엔티티 가져오기
		Plan plan = planRepository.findByIdAndUser_Id(planId, userAccount.getId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND));

		// 가져온 엔티티를 dto에 파싱
		TravelBudgetDto travelBudgetDto = TravelBudgetDto
				.builder()
				.title(plan.getTitle())
				.city(plan.getCity())
				.endDate(plan.getEndDate())
				.startDate(plan.getStartDate())
				.country(plan.getCountry())
				.build();

		// planId에 해당하는 카테고리별 경비 가져오기
		List<CategoryPlan> categoryPlanList = categoryPlanRepository.findAllByPlan_Id(planId);

		// 가져온 엔티티를 dto에 파싱
		List<CategoryPlanDto> categoryPlanDtoList = new ArrayList<>();
		for (CategoryPlan categoryPlan : categoryPlanList) {
			CategoryPlanDto categoryPlanDto = CategoryPlanDto
					.builder()
					.categoryId(categoryPlan.getCategory().getId())
					.categoryBalance(categoryPlan.getCategoryBalance())
					.categoryBudget(categoryPlan.getCategoryBudget())
					.build();
			categoryPlanDtoList.add(categoryPlanDto);
		}

		// 결제 내역 추출 및 그룹화 로직 추가
		List<PaymentHistory> paymentHistoryList =
				paymentHistoryRepository.findByUserIdAndPaymentDateBetween(userAccount.getId(), travelBudgetDto.getStartDate(), travelBudgetDto.getEndDate());
		Map<String, List<PlanPaymentHistoryDto>> paymentHistoryByDate = new LinkedHashMap<>();
		for (PaymentHistory paymentHistory : paymentHistoryList) {
			String paymentDate = paymentHistory.getCreatedAt().toString();
			Keymoney keymoney = keymoneyRepository.findById(paymentHistory.getKeymoneyId()).orElseThrow(
					() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY)
			);
			PlanPaymentHistoryDto paymentHistoryDto = PlanPaymentHistoryDto.builder()
					.price(paymentHistory.getPrice())
					.store(paymentHistory.getStore())
					.createdAt(paymentHistory.getCreatedAt())
					.id(paymentHistory.getId())
					.balance(paymentHistory.getBalance())
					.category(paymentHistory.getCategory())
					.lat(paymentHistory.getLat())
					.lng(paymentHistory.getLng())
					.address(paymentHistory.getAddress())
					.memo(paymentHistory.getMemo())
					.isPayment(paymentHistory.getIsPayment())
					.unit(keymoney.getUnit())
					.build();

			paymentHistoryByDate.computeIfAbsent(paymentDate, k -> new ArrayList<>()).add(paymentHistoryDto);
		}

		// map에 담아줌
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("travelBudget", travelBudgetDto);
		responseData.put("category", categoryPlanDtoList);
		responseData.put("timePaymentHistory", paymentHistoryByDate);

		// apiResponse에 담아서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result(responseData)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	//카테고리 정렬로 경비내역 보여주기
	public ResponseEntity<?> getPlanByCategory(String accessToken, int planId) {
		// accessToken으로 유저 검증
		User userAccount = userService.getUserByAccessToken(accessToken);

		// planId와 userId로 유저 소유 여부 식별 및 엔티티 가져오기
		Plan plan = planRepository.findByIdAndUser_Id(planId, userAccount.getId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND));

		// 가져온 엔티티를 dto에 파싱
		TravelBudgetDto travelBudgetDto = TravelBudgetDto
				.builder()
				.title(plan.getTitle())
				.city(plan.getCity())
				.endDate(plan.getEndDate())
				.startDate(plan.getStartDate())
				.country(plan.getCountry())
				.build();

		// planId에 해당하는 카테고리별 경비 가져오기
		List<CategoryPlan> categoryPlanList = categoryPlanRepository.findAllByPlan_Id(planId);

		// 가져온 엔티티를 dto에 파싱
		List<CategoryPlanDto> categoryPlanDtoList = new ArrayList<>();
		for (CategoryPlan categoryPlan : categoryPlanList) {
			CategoryPlanDto categoryPlanDto = CategoryPlanDto
					.builder()
					.categoryId(categoryPlan.getCategory().getId())
					.categoryBalance(categoryPlan.getCategoryBalance())
					.categoryBudget(categoryPlan.getCategoryBudget())
					.build();
			categoryPlanDtoList.add(categoryPlanDto);
		}

		// 결제 내역 추출 및 그룹화 로직 추가
		List<PaymentHistory> paymentHistoryList =
				paymentHistoryRepository.findByUserIdAndPaymentDateBetween(userAccount.getId(), travelBudgetDto.getStartDate(), travelBudgetDto.getEndDate());
		Map<String, List<PlanPaymentHistoryDto>> paymentHistoryByDate = new LinkedHashMap<>();
		for (PaymentHistory paymentHistory : paymentHistoryList) {
			Keymoney keymoney = keymoneyRepository.findById(paymentHistory.getKeymoneyId()).orElseThrow(
					() -> new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY)
			);
			String paymentCategory = paymentHistory.getCategory().toString();
			PlanPaymentHistoryDto paymentHistoryDto = PlanPaymentHistoryDto.builder()
					.price(paymentHistory.getPrice())
					.store(paymentHistory.getStore())
					.createdAt(paymentHistory.getCreatedAt())
					.id(paymentHistory.getId())
					.balance(paymentHistory.getBalance())
					.category(paymentHistory.getCategory())
					.lat(paymentHistory.getLat())
					.lng(paymentHistory.getLng())
					.address(paymentHistory.getAddress())
					.memo(paymentHistory.getMemo())
					.isPayment(paymentHistory.getIsPayment())
					.unit(keymoney.getUnit())
					.build();
			paymentHistoryByDate.computeIfAbsent(paymentCategory, k -> new ArrayList<>()).add(paymentHistoryDto);
		}

		// map에 담아줌
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("travelBudget", travelBudgetDto);
		responseData.put("category", categoryPlanDtoList);
		responseData.put("categoryPaymentHistory", paymentHistoryByDate);

		// apiResponse에 담아서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result(responseData)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	public ResponseEntity<?> deletePlan(String accessToken, int planId) {
		// accessToken으로 유저 검증
		User userAccount = userService.getUserByAccessToken(accessToken);

		// planId와 userId로 유저 소유 여부 식별 및 엔티티 가져오기
		Plan plan = planRepository.findByIdAndUser_Id(planId, userAccount.getId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND));

		// plan 삭제
		plan.softDeletePlan();

		// planId에 해당하는 카테고리별 경비 가쟈오기
		List<CategoryPlan> categoryPlans = categoryPlanRepository.findAllByPlan_Id(planId);

		// categoryPlan 삭제
		for (CategoryPlan categoryPlan : categoryPlans) {
			categoryPlan.softDeleteCategoryPlan();
		}

		// apiResponse에 담아서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result("삭제 성공")
				.resultCode(SuccessCode.DELETE_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Transactional
	// 카테고리별 경비 계획 제외한 수정
	public ResponseEntity<?> updatePlan(String accessToken, int planId, UpdatePlanDto updatePlanDto) {
		// accessToken으로 유저 검증
		User userAccount = userService.getUserByAccessToken(accessToken);

		// planId와 userId로 유저 소유 여부 식별 및 엔티티 가져오기
		Plan plan = planRepository.findByIdAndUser_Id(planId, userAccount.getId())
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND));

		// plan 업데이트
		plan.updatePlan(updatePlanDto);

		// apiResponse에 담아서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result("공통 경비계획 수정 성공")
				.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
	}

	@Transactional
	//카테고리별 경비 계획 수정
	public ResponseEntity<?> updateCategoryPlan(String accessToken, int planId, UpdateCategoryArrayDto updateCategoryArrayDto) {
		List<UpdateCategoryBudgetDto> updateCategoryBudgetDtoList = updateCategoryArrayDto.getCategory();
		for (UpdateCategoryBudgetDto updateCategoryBudgetDto : updateCategoryBudgetDtoList) {
			CategoryPlan categoryPlan = categoryPlanRepository.findByCategory_IdAndPlan_Id(updateCategoryBudgetDto.getCategoryId(), planId)
					.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.CATEGORY_PLAN_NOT_FOUND));
			categoryPlan.updateCategoryBudget(updateCategoryBudgetDto.getCategoryBudget());
		}

		// apiResponse에 담아서 리턴
		ApiResponse apiResponse = ApiResponse.builder()
				.result("카테고리 경비계획 수정 성공")
				.resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
	}

}
