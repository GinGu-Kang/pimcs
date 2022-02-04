package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,String> {

//    @EntityGraph(value = "User.userRoles")
    @EntityGraph(value = "Company.companyWorker")
    Optional<Company> findByCompanyCode(String companyCode);


}
