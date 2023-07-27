package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class TravelBudgetRepository {
    private final EntityManager em;
    public Plan findOne(Long id) {
        return em.find(Plan.class, id);
    }
}
