package com.example.travelhana.Repository;

import com.example.travelhana.Domain.CategoryPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryPlanRepository extends JpaRepository<CategoryPlan,Long> {

    List<CategoryPlan> findAllByPlan_Id(Long id);

    void deleteAllByPlan_Id(Long id);

    Optional<CategoryPlan> findByIdAndPlan_Id(Long Id, Long categoryPlanId);
}
