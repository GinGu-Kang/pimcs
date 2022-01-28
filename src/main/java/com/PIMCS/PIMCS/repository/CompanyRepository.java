package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,String> {
    Optional<Company> findByCompanyCode(String companyCode);


}
