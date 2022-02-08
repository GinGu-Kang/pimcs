package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
    public Optional<ProductCategory> findById(int id);
}
