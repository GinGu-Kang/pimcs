package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,String> {

    Optional<Product> findByProductCode(String prodCode);

    Optional<Product> findById(int id);

    @Override
//    @EntityGraph(attributePaths = {"company"})
    List<Product> findAll();
    List<Product> findByCompany(Company company);
    List<Product> findByProductCodeContaining(String prodCode);
}
