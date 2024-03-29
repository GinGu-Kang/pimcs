package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.domain.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    User getOne(String email);
    void deleteByEmail(String email);

    List<User> findAllByEmailIn(List<String> userEmailList);

    List<User> findByCompanyAndEmailIn(Company company,List<String> userEmailList);
//    @EntityGraph(value = "User.userRoles")
    List<User> findByCompany(Company company);
    List<User> findByCompanyAndDepartmentLike(Company company,String department);
    List<User> findByCompanyAndNameLike(Company company,String name);


    Page<User> findByNameLikeOrderByName(String name, Pageable pageable);
    Page<User> findByEmailLikeOrderByName(String email, Pageable pageable);
    Page<User> findByPhoneLikeOrderByName(String phone, Pageable pageable);
}
