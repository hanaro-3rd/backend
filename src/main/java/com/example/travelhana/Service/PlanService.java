package com.example.travelhana.Service;

import com.example.travelhana.Domain.Category;
import com.example.travelhana.Domain.CategoryPlan;
import com.example.travelhana.Domain.Plan;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.PlanDto;
import com.example.travelhana.Dto.PlanSuccessDto;
import com.example.travelhana.Repository.CategoryPlanRepository;
import com.example.travelhana.Repository.CategoryRepository;
import com.example.travelhana.Repository.PlanRepository;
import com.example.travelhana.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    //인젝션 생각하자
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryPlanRepository categoryPlanRepository;
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

        Optional<Category> category1 = categoryRepository.findById(1L); // findById null일 수도 있어서 optional<>에 걸어줌
        Optional<Category> category2 = categoryRepository.findById(2L);
        Optional<Category> category3 = categoryRepository.findById(3L);
        Optional<Category> category4 = categoryRepository.findById(4L);
        Optional<Category> category5 = categoryRepository.findById(5L);
        Optional<Category> category6 = categoryRepository.findById(6L);

        Plan returnPlan = planRepository.save(plan);  //save return이 클래스

        CategoryPlan categoryPlan1 = new CategoryPlan();
        CategoryPlan categoryPlan2 = new CategoryPlan();
        CategoryPlan categoryPlan3 = new CategoryPlan();
        CategoryPlan categoryPlan4 = new CategoryPlan();
        CategoryPlan categoryPlan5 = new CategoryPlan();
        CategoryPlan categoryPlan6 = new CategoryPlan();
        categoryPlan1.setPlan(returnPlan);
        categoryPlan1.setCategory(category1.get()); // optional이면 .get()으로 한 번 더 풀어야함
        categoryPlan1.setCategoryBudget(planDto.getCategoryBalance1());
        categoryPlan1.setCategoryBalance(planDto.getCategoryBalance1());

        categoryPlan2.setPlan(returnPlan);
        categoryPlan2.setCategory(category2.get()); // optional이면 .get()으로 한 번 더 풀어야함
        categoryPlan2.setCategoryBudget(planDto.getCategoryBalance2());
        categoryPlan2.setCategoryBalance(planDto.getCategoryBalance2());

        categoryPlan3.setPlan(returnPlan);
        categoryPlan3.setCategory(category3.get());
        categoryPlan3.setCategoryBudget(planDto.getCategoryBalance3());
        categoryPlan3.setCategoryBalance(planDto.getCategoryBalance3());

        categoryPlan4.setPlan(returnPlan);
        categoryPlan4.setCategory(category4.get());
        categoryPlan4.setCategoryBudget(planDto.getCategoryBalance4());
        categoryPlan4.setCategoryBalance(planDto.getCategoryBalance4());

        categoryPlan5.setPlan(returnPlan);
        categoryPlan5.setCategory(category5.get());
        categoryPlan5.setCategoryBudget(planDto.getCategoryBalance5());
        categoryPlan5.setCategoryBalance(planDto.getCategoryBalance5());

        categoryPlan6.setPlan(returnPlan);
        categoryPlan6.setCategory(category6.get());
        categoryPlan6.setCategoryBudget(planDto.getCategoryBalance6());
        categoryPlan6.setCategoryBalance(planDto.getCategoryBalance6());

        List<CategoryPlan> categoryPlanList = new ArrayList<>();
        categoryPlanList.add(categoryPlan1);
        categoryPlanList.add(categoryPlan2);
        categoryPlanList.add(categoryPlan3);
        categoryPlanList.add(categoryPlan4);
        categoryPlanList.add(categoryPlan5);
        categoryPlanList.add(categoryPlan6);
        categoryPlanRepository.saveAll(categoryPlanList);

        PlanSuccessDto planSuccessDto = new PlanSuccessDto();
        planSuccessDto.setUserId(user.get().getId());
        planSuccessDto.setPlanId(returnPlan.getId());
        return planSuccessDto;
    }
    public
}
