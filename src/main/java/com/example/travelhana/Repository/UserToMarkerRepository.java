package com.example.travelhana.Repository;

import com.example.travelhana.Domain.UserToMarker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToMarkerRepository extends JpaRepository<UserToMarker, Integer> {

	Boolean existsByUser_IdAndMarker_Id(int userId, int markerId);

}
