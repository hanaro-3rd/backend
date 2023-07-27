package com.example.travelhana.Service;

import com.example.travelhana.Domain.Category;
import com.example.travelhana.Domain.CategoryPlan;
import com.example.travelhana.Domain.Plan;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.*;
import com.example.travelhana.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    //인젝션 생각하자
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryPlanRepository categoryPlanRepository;
    private final TravelBudgetRepository travelBudgetRepository;
    //경비 생성
    public PlanSuccessDto savePlan(PlanDto planDto) {
        Plan plan = new Plan();
        plan.setTitle(planDto.getTitle());
        plan.setCity(planDto.getCity());
        plan.setStartDate(planDto.getStartDate());
        plan.setEndDate(planDto.getEndDate());
        plan.setCountry(planDto.getCountry());
        plan.setTotalBalance(planDto.getTotalBudget());
        plan.setTotalBudget(planDto.getTotalBudget());
        Optional<User> user = userRepository.findById(planDto.getUserId());
        plan.setUser(user.get());
        Plan returnPlan = planRepository.save(plan);  //save return이 클래스
        //category 테이블에 id,name이 다 존재한다는 조건에 분기처리
        if (!categoryRepository.existsById(1L)){
            Category nullCategory1 = new Category(1L,"식비");
            Category nullCategory2 = new Category(2L,"교통");
            Category nullCategory3 = new Category(3L,"숙박");
            Category nullCategory4 = new Category(4L,"쇼핑");
            Category nullCategory5 = new Category(5L,"문화");
            Category nullCategory6 = new Category(6L,"기타");
            List<Category> categoryList = new ArrayList<>();
            categoryList.add(nullCategory1);
            categoryList.add(nullCategory2);
            categoryList.add(nullCategory3);
            categoryList.add(nullCategory4);
            categoryList.add(nullCategory5);
            categoryList.add(nullCategory6);
            categoryRepository.saveAll(categoryList);
        }

        List<CategoryPlan> categoryPlanList = new ArrayList<>();
        List<UpdateCategoryBudgetDto> updateCategoryBudgetDtoList = planDto.getCategory();
        for(UpdateCategoryBudgetDto updateCategoryBudgetDto : updateCategoryBudgetDtoList) {
            CategoryPlan categoryPlan = new CategoryPlan();
            categoryPlan.setCategory(categoryRepository.getById(updateCategoryBudgetDto.getCategoryId()));
            categoryPlan.setPlan(returnPlan);
            categoryPlan.setCategoryBudget(updateCategoryBudgetDto.getCategoryBudget());
            categoryPlan.setCategoryBalance(updateCategoryBudgetDto.getCategoryBalance());
            categoryPlanList.add(categoryPlan);
        }

        categoryPlanRepository.saveAll(categoryPlanList);

        PlanSuccessDto planSuccessDto = new PlanSuccessDto();
        planSuccessDto.setUserId(user.get().getId());
        planSuccessDto.setPlanId(returnPlan.getId());
        return planSuccessDto;
    }
    public ResponseEntity<List<TravelElementDto>> getPlanList(Long id) {
        //userId에 해당하는 plan 테이블
        List<Plan> planList = planRepository.findAllByUser_Id(id);
        List<TravelElementDto> travelElementDtoList = new ArrayList<>();

        for (Plan plan : planList) {
            TravelElementDto travelElementDto = new TravelElementDto();
            travelElementDto.setPlanId(plan.getId());
            travelElementDto.setTitle(plan.getTitle());
            travelElementDto.setCountry(plan.getCountry());
            travelElementDto.setStartDate(plan.getStartDate());
            travelElementDto.setEndDate(plan.getEndDate());
            travelElementDto.setCity(plan.getCity());
            travelElementDto.setTotalBudget(plan.getTotalBudget());
            travelElementDto.setTotalBalance(plan.getTotalBalance());
            travelElementDtoList.add(travelElementDto);
        }
        return new ResponseEntity<>(travelElementDtoList, HttpStatus.OK);
        // 1. request의 키값을 읽어올수 있나
        // 2. 왜 existsByID를 통과하나
    }
    public ResponseEntity<Map<String, Object>> getPlan(Long id) {
        Optional <Plan> plan = planRepository.findById(id);
        TravelBudgetDto travelBudgetDto = new TravelBudgetDto();
        travelBudgetDto.setTitle(plan.get().getTitle());
        travelBudgetDto.setCity(plan.get().getCity());
        travelBudgetDto.setEndDate(plan.get().getEndDate());
        travelBudgetDto.setStartDate(plan.get().getStartDate());
        travelBudgetDto.setCountry(plan.get().getCountry());
        List<CategoryPlan> categoryPlanList = categoryPlanRepository.findAllByPlan_Id(id);
        List<CategoryPlanDto> categoryPlanDtoList = new ArrayList<>();
        for(CategoryPlan categoryPlan : categoryPlanList) {
            CategoryPlanDto categoryPlanDto = new CategoryPlanDto();
            categoryPlanDto.setCategoryId(categoryPlan.getCategory().getId());
            categoryPlanDto.setCategoryBalance(categoryPlan.getCategoryBalance());
            categoryPlanDto.setCategoryBudget(categoryPlan.getCategoryBudget());
            categoryPlanDtoList.add(categoryPlanDto);
        }
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("travelBudget", travelBudgetDto);
        responseData.put("category", categoryPlanDtoList);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
    public String deletePlan(Long id) {
        categoryPlanRepository.deleteAllByPlan_Id(id);
        planRepository.deleteById(id);
        return "Success Delete";
    }

    @Transactional
    public String updatePlan(UpdateTravelBudgetDto updateTravelBudgetDto) {
        Optional <Plan> plan = planRepository.findById(updateTravelBudgetDto.getPlanId());
        plan.get().updatePlan(updateTravelBudgetDto);
        planRepository.save(plan.get());
        return "Update Success";
    }

    @Transactional
    public String updateCategoryPlan(Long plan_Id, UpdateCategoryArrayDto updateCategoryArrayDto) {
        List<UpdateCategoryBudgetDto> updateCategoryBudgetDtoList = updateCategoryArrayDto.getCategory();
        for(UpdateCategoryBudgetDto updateCategoryBudgetDto : updateCategoryBudgetDtoList) {
            Optional <CategoryPlan> categoryPlan = categoryPlanRepository.findByIdAndPlan_Id(updateCategoryBudgetDto.getCategoryId(),plan_Id);
            categoryPlan.get().updateCategoryBudget(updateCategoryBudgetDto);
            categoryPlanRepository.save(categoryPlan.get());
        }
        return "CategoryPlan Update Success";
    }
    // Illegal
}
