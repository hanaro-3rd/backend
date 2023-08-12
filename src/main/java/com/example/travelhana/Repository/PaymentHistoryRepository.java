package com.example.travelhana.Repository;

import com.example.travelhana.Domain.PaymentHistory;
import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {

	List<PaymentHistory> findAllByKeymoneyId(int id);

	Optional<PaymentHistory> findByIdAndUserId(Long id, int getUserId);
	@Query("SELECT ph FROM PaymentHistory ph WHERE ph.userId = :userId AND ph.createdAt BETWEEN :startDate AND :endDate")
	List<PaymentHistory> findByUserIdAndPaymentDateBetween(Integer userId, LocalDateTime startDate, LocalDateTime  endDate);

}
