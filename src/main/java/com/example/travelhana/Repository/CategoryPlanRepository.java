package com.example.travelhana.Repository;

import com.example.travelhana.Domain.CategoryPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryPlanRepository extends JpaRepository<CategoryPlan, Integer> {

	List<CategoryPlan> findAllByPlan_Id(Integer id);

	Optional<CategoryPlan> findByIdAndPlan_Id(Integer Id, Integer categoryPlanId);

	Optional<CategoryPlan> findByCategory_IdAndPlan_Id(int categoryId, int planId);

}
