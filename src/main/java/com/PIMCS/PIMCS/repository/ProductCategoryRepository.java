package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
    Optional<ProductCategory> findById(int id);
    List<ProductCategory> findByCompany(Company company);

    Optional<ProductCategory> findByIdAndCompany(int id, Company company);
}
