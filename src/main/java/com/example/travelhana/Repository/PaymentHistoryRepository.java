package com.example.travelhana.Repository;

import com.example.travelhana.Domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

	List<PaymentHistory> findAllByKeymoneyId(int keymoneyId);
	List<PaymentHistory> findAllByKeymoneyIdAndIsPayment(int keymoneyId, Boolean isPayment);

	Optional<PaymentHistory> findByIdAndUserId(Long id, int getUserId);

}
