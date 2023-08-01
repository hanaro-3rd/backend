package com.example.travelhana.Repository;

import com.example.travelhana.Domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Integer> {
}
