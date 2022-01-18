package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<User,String> {

    Optional<User> findByEmail(String email);
}
