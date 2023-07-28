package com.example.travelhana.Repository;

import com.example.travelhana.Domain.ExchangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory,Long> {
}
