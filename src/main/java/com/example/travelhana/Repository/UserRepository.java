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


	@Modifying
	@Query(value = "UPDATE user SET refresh_token = NULL WHERE device_id = :deviceId", nativeQuery = true)
	void updateRefreshToken(String deviceId);

	@Modifying
	@Query(value = "UPDATE user SET password =:password WHERE device_id = :deviceId", nativeQuery = true)
	void updatePassword(@Param("deviceId") String deviceId, @Param("password")String password);

	Optional<User> findByDeviceId(String deviceId);

	boolean existsByDeviceId(String deviceId);
	boolean existsByRegistrationNum(String registrationNum);
	Optional<User> findByNameAndRegistrationNum(String name,String registrationNum);

	Optional<User> findByIdAndIsWithdrawal(int userId, Boolean isWithdrawal);

	Optional<User> findByPhoneNum(String phonenum);


}

