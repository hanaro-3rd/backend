package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {
    List<Plan> findAllByUser_Id(Long id);
}
