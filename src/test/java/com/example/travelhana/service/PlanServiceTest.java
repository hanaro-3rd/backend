package com.example.travelhana.service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Plan.*;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.*;
import com.example.travelhana.Service.implement.PlanServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@Transactional
@DisplayName("경비 계획 단위 테스트")
@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class PlanServiceTest {

	@InjectMocks
	private PlanServiceImpl planService;

	@Mock
	private KeymoneyRepository keymoneyRepository;

	@Mock
	private PlanRepository planRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private PaymentHistoryRepository paymentHistoryRepository;

	@Mock
	private CategoryPlanRepository categoryPlanRepository;

	@Test
	@DisplayName("경비 계획 작성 테스트")
	public void savePlanTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		List<CategoryBudgetDto> categoryBudgetDtos = new ArrayList<>();
		categoryBudgetDtos.add(new CategoryBudgetDto(1, 10L));

		Category category1 = new Category(1, "식비");

		PlanDto planDto = PlanDto
				.builder()
				.totalBudget(210L)
				.title("test title")
				.country("test country")
				.city("test city")
				.category(categoryBudgetDtos)
				.build();

		Plan plan = Plan.builder()
				.id(1)
				.title(planDto.getTitle())
				.city(planDto.getCity())
				.startDate(planDto.getStartDate())
				.endDate(planDto.getEndDate())
				.country(planDto.getCountry())
				.totalBudget(planDto.getTotalBudget()).users(users).build();

		List<CategoryPlan> categoryPlans = new ArrayList<>();
		categoryPlans.add(new CategoryPlan(1, plan, category1, categoryBudgetDtos.get(0).getCategoryBudget(), false));

		// stub
		given(planRepository.save(any(Plan.class))).willReturn(plan);
		given(categoryRepository.findById(1)).willReturn(Optional.of(category1));
		given(categoryPlanRepository.saveAll(categoryPlans)).willReturn(categoryPlans);

		// when
		ResponseEntity<?> responseEntity = planService.savePlan(users, planDto);

		// then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		PlanSuccessDto planSuccessDto = (PlanSuccessDto) apiResponse.getResult();

		assertEquals(planSuccessDto.getUserId(), 1);
		assertEquals(planSuccessDto.getPlanId(), 1);

		assertEquals(SuccessCode.INSERT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.INSERT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("경비 계획 목록 불러오기 테스트")
	public void getPlanListTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		List<Plan> planList = new ArrayList<>();

		Plan plan1 = Plan.builder()
				.id(1)
				.title("test title 1")
				.country("test country 1")
				.city("test city 1")
				.build();
		planList.add(plan1);

		Plan plan2 = Plan.builder()
				.id(2)
				.title("test title 2")
				.country("test country 2")
				.city("test city 2")
				.build();
		planList.add(plan2);

		// stub
		given(planRepository.findAllByUser_IdAndIsDeletedFalse(users.getId())).willReturn(planList);

		// when
		ResponseEntity<?> responseEntity = planService.getPlanList(users);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		List<TravelElementDto> planSuccessDto = (List<TravelElementDto>) apiResponse.getResult();

		assertEquals(planSuccessDto.size(), 2);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("경비 계획 결제 내역 불러오기 테스트 (날짜별)")
	public void getPlanTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int planId = 1;

		Category category1 = new Category(1, "식비");
		Category category2 = new Category(2, "교통");

		Plan plan = Plan.builder()
				.id(1)
				.title("test title 1")
				.country("test country 1")
				.city("test city 1")
				.startDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)))
				.startDate(LocalDateTime.of(LocalDate.now().plusDays(10), LocalTime.of(0,0,0)))
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		List<CategoryPlan> categoryPlans = new ArrayList<>();
		categoryPlans.add(new CategoryPlan(1, plan, category1, 100L, false));
		categoryPlans.add(new CategoryPlan(2, plan, category2, 200L, false));

		List<PaymentHistory> paymentHistoryList = new ArrayList<>();
		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.keymoneyId(keymoney.getId())
				.createdAt(LocalDateTime.of(LocalDate.now().plusDays(5), LocalTime.of(0,0,0)))
				.build();
		paymentHistoryList.add(paymentHistory);

		// stub
		given(planRepository.findByIdAndUsers_Id(planId, users.getId())).willReturn(Optional.ofNullable(plan));
		given(categoryPlanRepository.findAllByPlan_Id(planId)).willReturn(categoryPlans);
		given(paymentHistoryRepository.findByUserIdAndPaymentDateBetween(users.getId(), plan.getStartDate(), plan.getEndDate())).willReturn(paymentHistoryList);
		given(keymoneyRepository.findById(paymentHistory.getKeymoneyId())).willReturn(Optional.of(keymoney));

		// when
		ResponseEntity<?> responseEntity = planService.getPlan(users, planId);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		Map<String, Object> responseData = (Map<String, Object>) apiResponse.getResult();

		TravelBudgetDto travelBudgetDto = (TravelBudgetDto) responseData.get("travelBudget");
		assertEquals(travelBudgetDto.getTitle(), "test title 1");
		assertEquals(travelBudgetDto.getCountry(), "test country 1");
		assertEquals(travelBudgetDto.getCity(), "test city 1");

		List<CategoryPlanDto> categoryPlanDtoList = (List<CategoryPlanDto>) responseData.get("category");
		assertEquals(categoryPlanDtoList.size(), 2);

		Map<String, List<PlanPaymentHistoryDto>> paymentHistoryByDate = (Map<String, List<PlanPaymentHistoryDto>>) responseData.get("timePaymentHistory");
		assertEquals(paymentHistoryByDate.get(paymentHistory.getCreatedAt().toString()).size(), 1);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("경비 계획 결제 내역 불러오기 테스트 (카테고리별)")
	public void getPlanByCategoryTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int planId = 1;

		Category category1 = new Category(1, "식비");
		Category category2 = new Category(2, "교통");

		Plan plan = Plan.builder()
				.id(1)
				.title("test title 1")
				.country("test country 1")
				.city("test city 1")
				.startDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)))
				.startDate(LocalDateTime.of(LocalDate.now().plusDays(10), LocalTime.of(0,0,0)))
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		List<CategoryPlan> categoryPlans = new ArrayList<>();
		categoryPlans.add(new CategoryPlan(1, plan, category1, 100L, false));
		categoryPlans.add(new CategoryPlan(2, plan, category2, 200L, false));

		List<PaymentHistory> paymentHistoryList = new ArrayList<>();
		PaymentHistory paymentHistory = PaymentHistory
				.builder()
				.id(1L)
				.keymoneyId(keymoney.getId())
				.category("test category")
				.createdAt(LocalDateTime.of(LocalDate.now().plusDays(5), LocalTime.of(0,0,0)))
				.build();
		paymentHistoryList.add(paymentHistory);

		// stub
		given(planRepository.findByIdAndUsers_Id(planId, users.getId())).willReturn(Optional.ofNullable(plan));
		given(categoryPlanRepository.findAllByPlan_Id(planId)).willReturn(categoryPlans);
		given(paymentHistoryRepository.findByUserIdAndPaymentDateBetween(users.getId(), plan.getStartDate(), plan.getEndDate())).willReturn(paymentHistoryList);
		given(keymoneyRepository.findById(paymentHistory.getKeymoneyId())).willReturn(Optional.of(keymoney));

		// when
		ResponseEntity<?> responseEntity = planService.getPlanByCategory(users, planId);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		Map<String, Object> responseData = (Map<String, Object>) apiResponse.getResult();

		TravelBudgetDto travelBudgetDto = (TravelBudgetDto) responseData.get("travelBudget");
		assertEquals(travelBudgetDto.getTitle(), "test title 1");
		assertEquals(travelBudgetDto.getCountry(), "test country 1");
		assertEquals(travelBudgetDto.getCity(), "test city 1");

		List<CategoryPlanDto> categoryPlanDtoList = (List<CategoryPlanDto>) responseData.get("category");
		assertEquals(categoryPlanDtoList.size(), 2);

		Map<String, List<PlanPaymentHistoryDto>> paymentHistoryByDate = (Map<String, List<PlanPaymentHistoryDto>>) responseData.get("categoryPaymentHistory");
		assertEquals(paymentHistoryByDate.get(paymentHistory.getCategory().toString()).size(), 1);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("경비 계획 삭제 테스트")
	public void deletePlanTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int planId = 1;

		Category category1 = new Category(1, "식비");
		Category category2 = new Category(2, "교통");

		Plan plan = Plan.builder()
				.id(1)
				.title("test title 1")
				.country("test country 1")
				.city("test city 1")
				.startDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)))
				.startDate(LocalDateTime.of(LocalDate.now().plusDays(10), LocalTime.of(0,0,0)))
				.build();

		List<CategoryPlan> categoryPlans = new ArrayList<>();
		categoryPlans.add(new CategoryPlan(1, plan, category1, 100L, false));
		categoryPlans.add(new CategoryPlan(2, plan, category2, 200L, false));

		// stub
		given(planRepository.findByIdAndUsers_Id(planId, users.getId())).willReturn(Optional.ofNullable(plan));
		given(categoryPlanRepository.findAllByPlan_Id(planId)).willReturn(categoryPlans);

		// when
		ResponseEntity<?> responseEntity = planService.deletePlan(users, planId);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		assertEquals(apiResponse.getResult(), "삭제 성공");

		assertEquals(SuccessCode.DELETE_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.DELETE_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("경비 계획 여행 정보 수정 테스트")
	public void updatePlanTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int planId = 1;

		UpdatePlanDto updatePlanDto = UpdatePlanDto
				.builder()
				.title("updated title")
				.country("updated country")
				.city("updated city")
				.build();

		Plan plan = Plan.builder()
				.id(1)
				.title("test title 1")
				.country("test country 1")
				.city("test city 1")
				.startDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)))
				.startDate(LocalDateTime.of(LocalDate.now().plusDays(10), LocalTime.of(0,0,0)))
				.build();

		// stub
		given(planRepository.findByIdAndUsers_Id(planId, users.getId())).willReturn(Optional.ofNullable(plan));

		// when
		ResponseEntity<?> responseEntity = planService.updatePlan(users, planId, updatePlanDto);

		// then
		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		assertEquals(apiResponse.getResult(), "공통 경비계획 수정 성공");

		assertEquals(SuccessCode.UPDATE_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.UPDATE_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("경비 계획 카테고리 정보 수정 테스트")
	public void updateCategoryPlanTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int planId = 1;

		List<UpdateCategoryBudgetDto> category = new ArrayList<>();
		category.add(new UpdateCategoryBudgetDto(1, 20L));
		category.add(new UpdateCategoryBudgetDto(2, 40L));

		Category category1 = new Category(1, "식비");
		Category category2 = new Category(2, "교통");

		Plan plan = Plan.builder()
				.id(1)
				.title("test title 1")
				.country("test country 1")
				.city("test city 1")
				.startDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)))
				.startDate(LocalDateTime.of(LocalDate.now().plusDays(10), LocalTime.of(0,0,0)))
				.build();

		List<CategoryPlan> categoryPlans = new ArrayList<>();
		categoryPlans.add(new CategoryPlan(1, plan, category1, category.get(0).getCategoryBudget(), false));
		categoryPlans.add(new CategoryPlan(2, plan, category2, category.get(1).getCategoryBudget(), false));

		// stub
		given(planRepository.findByIdAndUsers_Id(planId, users.getId())).willReturn(Optional.ofNullable(plan));
		given(categoryPlanRepository.findByCategory_IdAndPlan_Id(1, planId)).willReturn(Optional.ofNullable(categoryPlans.get(0)));
		given(categoryPlanRepository.findByCategory_IdAndPlan_Id(2, planId)).willReturn(Optional.ofNullable(categoryPlans.get(1)));

		// when
		ResponseEntity<?> responseEntity = planService.updateCategoryPlan(users, planId, new UpdateCategoryArrayDto(category));

		// then
		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		assertEquals(apiResponse.getResult(), "카테고리 경비계획 수정 성공");

		assertEquals(SuccessCode.UPDATE_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.UPDATE_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

}
