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
//    @EntityGraph(attributePaths = {"ownDevice"})
//    Optional<Company> findByCompanyId(Integer companyId);
    @EntityGraph(value = "Company.companyWorker")
    Optional<Company> findByCompanyCode(String companyCode);
    Page<Company> findByCompanyCodeLike(String companyCode, Pageable pageable);
    Page<Company> findByCompanyNameLike(String companyName, Pageable pageable);
}
