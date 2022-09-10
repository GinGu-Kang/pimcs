package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Integer> {

//    @EntityGraph(value = "User.userRoles")
//    @EntityGraph(attributePaths = {"ownDevice"})
//    Optional<Company> findByCompanyId(Integer companyId);
    @EntityGraph(value = "Company.companyWorker")
    Optional<Company> findByCompanyCode(String companyCode);
    Optional<Company> findByCompanyName(String companyName);
    Optional<Company> findByIdAndCompanyCode(int id, String companyCode);
    Page<Company> findByCompanyCodeLikeOrderById(String companyCode, Pageable pageable);
    Page<Company> findByCompanyNameLikeOrderById(String companyName, Pageable pageable);



    @Query(
            value = "select c from Company c where c.id in (" +
            "select o.company.id from OwnDevice o where o.serialNumber like %:serialNumber%)",
            countQuery = "select COUNT(c) from Company c where c.id in(" +
                    "select o.company.id from OwnDevice o where o.serialNumber like %:serialNumber%)"
    )
    Page<Company> findByOwnDeviceSerial(@Param("serialNumber") String serialNumber, Pageable pageable);
    Company getOne(Integer companyId);
}
