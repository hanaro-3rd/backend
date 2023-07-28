package com.example.travelhana.Repository;

import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByIdAndIsWithdrawal(int userId, Boolean isWithdrawal);

}