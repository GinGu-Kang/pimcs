package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Integer> {

//    @EntityGraph(value = "User.userRoles")
    @EntityGraph(value = "Company.companyWorker")
    Optional<Company> findByCompanyCode(String companyCode);
    Page<Question> findByCompanyNameLike(String compnayName, Pageable pageable);


}
