package com.example.travelhana.Repository;

import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {


	@Modifying
	@Query(value = "UPDATE user SET refresh_token = NULL WHERE device_id = :deviceId", nativeQuery = true)
	void updateRefreshToken(String deviceId);

	Optional<User> findByDeviceId(String deviceId);

	boolean existsByDeviceId(String deviceId);
	boolean existsByRegistrationNum(String registrationNum);

	Optional<User> findByIdAndIsWithdrawal(int userId, Boolean isWithdrawal);

	Optional<User> findByPhoneNum(String phonenum);


}

