package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    List<Plan> findAllByUser_Id(int id);

    Optional<Plan> findByIdAndUser_Id(Integer id, Integer userId);

    @Query("SELECT pl " +
            "FROM Plan pl " +
            "WHERE pl.user.id = :userId AND pl.isDeleted = false " +
            "ORDER BY pl.startDate DESC")
    List<Plan> findAllByUser_IdAndIsDeletedFalse(@Param("userId") int userId);

}
