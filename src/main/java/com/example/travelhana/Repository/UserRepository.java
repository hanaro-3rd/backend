package com.example.travelhana.Repository;

import com.example.travelhana.Domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByDeviceId(String deviceId);

	boolean existsByDeviceId(String deviceId);

	boolean existsByRegistrationNum(String registrationNum);

	Optional<User> findByPhoneNum(String phonenum);

}

