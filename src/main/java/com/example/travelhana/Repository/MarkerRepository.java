package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker, Integer> {

	List<Marker> findAllByUnit(String unit);

}