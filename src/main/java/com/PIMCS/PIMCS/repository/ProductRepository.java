package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,String> {

    Optional<Product> findByProductCode(String prodCode);

    Optional<Product> findById(int id);

    Optional<Product> findByIdAndCompany(int id,Company company);

    List<Product> findAll();
    @EntityGraph(attributePaths = {"company","productCategory"})
    List<Product> findByCompany(Company company);

    @EntityGraph(attributePaths = {"company","productCategory"})
    Page<Product> findByCompanyOrderByCreatedAtDesc(Company company, Pageable pageable);

    List<Product> findByProductCodeContaining(String prodCode);
}
