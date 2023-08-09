package com.example.travelhana.Repository;

import com.example.travelhana.Domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {

	List<PaymentHistory> findAllByKeyMoneyId(int id);

	PaymentHistory findByIdAndUserId(Long id, int getUserId);

}
