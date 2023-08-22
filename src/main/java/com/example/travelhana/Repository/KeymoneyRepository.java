package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Keymoney;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KeymoneyRepository extends JpaRepository<Keymoney, Integer> {

	Optional<Keymoney> findByUsers_IdAndUnit(int userId, String unit);

	List<Keymoney> findByUsers_Id(int userId);

}
