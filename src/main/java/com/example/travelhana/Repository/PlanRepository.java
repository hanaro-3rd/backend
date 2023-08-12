package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Plan;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    List<Plan> findAllByUser_Id(int id);



    Optional<Plan> findByIdAndUser_Id(Integer id, Integer userId);
}
