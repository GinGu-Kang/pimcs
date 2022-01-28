package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    @EntityGraph(value = "User.userRoles")
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
    @EntityGraph(value = "User.userRoles")
    List<User> findByCompanyCode(String companyCode);
}
