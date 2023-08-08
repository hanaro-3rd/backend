package com.example.travelhana.Repository;

import com.example.travelhana.Domain.ExchangeRate;
import com.example.travelhana.Domain.ExchangeReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeReservationRepository extends JpaRepository<ExchangeReservation, Long> {
}
