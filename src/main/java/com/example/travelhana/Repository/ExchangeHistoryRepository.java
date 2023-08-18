package com.example.travelhana.Repository;

import com.example.travelhana.Domain.ExchangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, Long> {

	List<ExchangeHistory> findAllByKeymoneyId(int keymoneyId);
	List<ExchangeHistory> findAllByKeymoneyIdAndIsBought(int keymoneyId, Boolean isBought);

	Optional<ExchangeHistory> findByIdAndUserId(Long id, int getUserId);

}
