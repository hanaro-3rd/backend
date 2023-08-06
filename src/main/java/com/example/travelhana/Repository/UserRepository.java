package com.example.travelhana.Repository;

import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


	Optional<User> findByDeviceId(String deviceId);

	boolean existsByDeviceId(String deviceId);

	Optional<User> findByIdAndIsWithdrawal(int userId, Boolean isWithdrawal);

	Optional<User> findByPhoneNum(String phonenum);


}

