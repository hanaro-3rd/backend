package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String name);

	Optional<Role> findById(Long id);

	boolean existsByName(String roleName);
}