package com.example.travelhana.Repository;

import com.example.travelhana.Domain.KeyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeyMoneyRepository extends JpaRepository<KeyMoney,Integer> {
    @Query("SELECT km FROM KeyMoney km WHERE km.user.id = :userId AND km.unit = :unit")
    KeyMoney findByUserIdAndUnit(@Param("userId") int userId, @Param("unit") String unit);

}
