package com.example.travelhana.Repository;

import com.example.travelhana.Domain.MarkerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkerHistoryRepository extends JpaRepository<MarkerHistory, Long> {

	List<MarkerHistory> findAllByKeymoneyId(int keymoneyId);

	Optional<MarkerHistory> findByIdAndUserId(Long Long, int userId);

}