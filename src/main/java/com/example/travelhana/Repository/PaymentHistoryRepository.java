package com.example.travelhana.Repository;

import com.example.travelhana.Domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

	List<PaymentHistory> findAllByKeymoneyId(int keymoneyId);
	List<PaymentHistory> findAllByKeymoneyIdAndIsPayment(int keymoneyId, Boolean isPayment);

	Optional<PaymentHistory> findByIdAndUserId(Long id, int getUserId);

	@Query("SELECT ph " +
			"FROM PaymentHistory ph " +
			"WHERE ph.userId = :userId AND ph.createdAt " +
			"BETWEEN :startDate AND :endDate " +
			"ORDER BY ph.createdAt ASC")
	List<PaymentHistory> findByUserIdAndPaymentDateBetween(
			@Param("userId") int userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
