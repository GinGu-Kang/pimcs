package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {
    List<UserRole> findByUser(String username);

}
