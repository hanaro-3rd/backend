package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<Users, Integer> {

	Optional<Users> findByDeviceId(String deviceId);
	Optional<Users> findByPhoneNum(String phonenum);
	List<Users> findAllByDeviceId(String deviceId);



}

