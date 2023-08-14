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
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryPlanRepository categoryPlanRepository;
    private final UserService userService;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final KeymoneyRepository keymoneyRepository;
    //경비 생성
    public ResponseEntity<?> savePlan(String accessToken, PlanDto planDto) {
        try{
            //유저 식별
            User userAccount = userService.getUserByAccessToken(accessToken);
            Plan plan = Plan.builder()
                    .title(planDto.getTitle())
                    .city(planDto.getCity())
                    .startDate(planDto.getStartDate())
                    .endDate(planDto.getEndDate())
                    .country(planDto.getCountry())
                    .totalBalance(planDto.getTotalBudget())
                    .totalBudget(planDto.getTotalBudget()).user(userAccount).build();
            //경비 계획 저장
            Plan returnPlan = planRepository.save(plan);

            //카테고리 테이블이 존재하지 않으면 생성
            if (!categoryRepository.existsById(1)){
                Category nullCategory1 = new Category(1,"식비");
                Category nullCategory2 = new Category(2,"교통");
                Category nullCategory3 = new Category(3,"숙박");
                Category nullCategory4 = new Category(4,"쇼핑");
                Category nullCategory5 = new Category(5,"문화");
                Category nullCategory6 = new Category(6,"기타");
                List<Category> categoryList = new ArrayList<>();
                categoryList.add(nullCategory1);
                categoryList.add(nullCategory2);
                categoryList.add(nullCategory3);
                categoryList.add(nullCategory4);
                categoryList.add(nullCategory5);
                categoryList.add(nullCategory6);
                categoryRepository.saveAll(categoryList);
            }
            //경비 계획에 필요한 카테고리명, 카테고리 경비, 카테고리 잔액 저장
            List<CategoryPlan> categoryPlanList = new ArrayList<>();
            List<CategoryBudgetDto> updateCategoryBudgetDtoList = planDto.getCategory();
            for(CategoryBudgetDto updateCategoryBudgetDto : updateCategoryBudgetDtoList) {
                CategoryPlan categoryPlan = CategoryPlan.builder()
                        .category(categoryRepository.findById(updateCategoryBudgetDto.getCategoryId()).orElseThrow(()-> new BusinessExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND)))
                        .plan(returnPlan)
                        .categoryBalance(updateCategoryBudgetDto.getCategoryBudget())
                        .categoryBudget(updateCategoryBudgetDto.getCategoryBudget())
                        .build();
                categoryPlanList.add(categoryPlan);
            }
            categoryPlanRepository.saveAll(categoryPlanList);

            //성공시 userId와 생성한 planId
            PlanSuccessDto planSuccessDto = PlanSuccessDto
                    .builder()
                    .userId(userAccount.getId())
                    .planId(returnPlan.getId())
                    .build();

            ApiResponse apiResponse = ApiResponse.builder()
                    .result(planSuccessDto)
                    .resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

        } catch(BusinessExceptionHandler e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .errorCode(e.getErrorCode().getStatusCode())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //경비계획 리스트 읽기
    public ResponseEntity<?> getPlanList(String accessToken) {
        try{
            User userAccount = userService.getUserByAccessToken(accessToken);
            //유적가 가지고 있는 경비계획 리스트 가져오기
            List<Plan> planList = planRepository.findAllByUser_IdAndIsDeletedFalse(userAccount.getId());
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
            ApiResponse apiResponse = ApiResponse.builder()
                    .result(travelElementDtoList)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch(BusinessExceptionHandler e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .errorCode(e.getErrorCode().getStatusCode())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //경비 계획 상세 읽어오기
    public ResponseEntity<?> getPlan(String accessToken, int id) {
        User userAccount = userService.getUserByAccessToken(accessToken);
        Plan plan = planRepository.findByIdAndUser_Id(id, userAccount.getId()).orElseThrow(
                ()-> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND)
        );
        TravelBudgetDto travelBudgetDto = TravelBudgetDto
                .builder()
                .title(plan.getTitle())
                .city(plan.getCity())
                .endDate(plan.getEndDate())
                .startDate(plan.getStartDate())
                .country(plan.getCountry())
                .build();
        List<CategoryPlan> categoryPlanList = categoryPlanRepository.findAllByPlan_Id(id);
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

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("travelBudget", travelBudgetDto);
        responseData.put("category", categoryPlanDtoList);
        // 결제 내역 추출 및 그룹화 로직 추가
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findByUserIdAndPaymentDateBetween(userAccount.getId(), travelBudgetDto.getStartDate(), travelBudgetDto.getEndDate());
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
        responseData.put("timePaymentHistory", paymentHistoryByDate);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(responseData)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    //카테고리 정렬로 경비내역 보여주기
    public ResponseEntity<?> getPlanByCategory(String accessToken, int id) {
        User userAccount = userService.getUserByAccessToken(accessToken);
        Plan plan = planRepository.findByIdAndUser_Id(id, userAccount.getId()).orElseThrow(
                () -> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND)
        );
        TravelBudgetDto travelBudgetDto = TravelBudgetDto
                .builder()
                .title(plan.getTitle())
                .city(plan.getCity())
                .endDate(plan.getEndDate())
                .startDate(plan.getStartDate())
                .country(plan.getCountry())
                .build();
        List<CategoryPlan> categoryPlanList = categoryPlanRepository.findAllByPlan_Id(id);
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
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("travelBudget", travelBudgetDto);
        responseData.put("category", categoryPlanDtoList);
        // 결제 내역 추출 및 그룹화 로직 추가
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findByUserIdAndPaymentDateBetween(userAccount.getId(), travelBudgetDto.getStartDate(), travelBudgetDto.getEndDate());
        Map<String, List<PlanPaymentHistoryDto>> paymentHistoryByDate = new LinkedHashMap<>();
        for (PaymentHistory paymentHistory : paymentHistoryList) {
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
                    .build();

            paymentHistoryByDate.computeIfAbsent(paymentCategory, k -> new ArrayList<>()).add(paymentHistoryDto);
        }
        responseData.put("categoryPaymentHistory", paymentHistoryByDate);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(responseData)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    public ResponseEntity<?> deletePlan(String accessToken, int id) {
        categoryPlanRepository.updateByPlan_Id(id);
        planRepository.updateById(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .result("삭제 성공")
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Transactional
    // 카테고리별 경비 계획 제외한 수정
    public ResponseEntity<?> updatePlan(String accessToken, UpdateTravelBudgetDto updateTravelBudgetDto) {
        Plan plan = planRepository.findById(updateTravelBudgetDto.getPlanId()).orElseThrow(
                ()-> new BusinessExceptionHandler(ErrorCode.PLAN_NOT_FOUND)
        );
        plan.updatePlan(updateTravelBudgetDto);
        planRepository.save(plan);
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
        for(UpdateCategoryBudgetDto updateCategoryBudgetDto : updateCategoryBudgetDtoList) {
            CategoryPlan categoryPlan = categoryPlanRepository.findByCategory_IdAndPlan_Id(updateCategoryBudgetDto.getCategoryId(),planId)
                            .orElseThrow(  ()-> new BusinessExceptionHandler(ErrorCode.CATEGORY_PLAN_NOT_FOUND));
            categoryPlan.updateCategoryBudget(updateCategoryBudgetDto);
            categoryPlanRepository.save(categoryPlan);
        }
        ApiResponse apiResponse = ApiResponse.builder()
                .result("카테고리 경비계획 수정 성공")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }



}
