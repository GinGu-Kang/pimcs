package com.PIMCS.PIMCS.repository;


import com.PIMCS.PIMCS.domain.Role;
import org.aspectj.apache.bcel.util.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,String> {
    Role findByName(String name);
}
