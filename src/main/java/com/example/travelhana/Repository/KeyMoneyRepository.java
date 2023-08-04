package com.example.travelhana.Repository;

import com.example.travelhana.Domain.KeyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KeyMoneyRepository extends JpaRepository<KeyMoney, Integer> {

	Optional<KeyMoney> findByUser_IdAndUnit(int userId, String unit);

}
