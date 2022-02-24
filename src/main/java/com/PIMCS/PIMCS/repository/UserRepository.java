package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
//    @EntityGraph(value = "User.userRoles")
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);

//    @EntityGraph(value = "User.userRoles")
    List<User> findByCompany(Company company);
    List<User> findByCompanyAndDepartment(Company company,String department);
    List<User> findByCompanyAndName(Company company,String name);
}
