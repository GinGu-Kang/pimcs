package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,String> {

    Optional<Product> findByProductCode(String prodCode);

    Optional<Product> findById(int id);

    Optional<Product> findByIdAndCompany(int id,Company company);
    Optional<Product> findByProductNameAndCompany(String productName, Company company);
    Optional<Product> findByProductCodeAndCompany(String productCode, Company company);

    List<Product> findAll();
    @EntityGraph(attributePaths = {"company","productCategory"})
    List<Product> findByCompany(Company company);
    @EntityGraph(attributePaths = {"productCategory"})
    List<Product> findByCompanyAndIdIn(Company company, List<Integer> idList);

    @EntityGraph(attributePaths = {"productCategory"})
    Page<Product> findByCompanyOrderByCreatedAtDesc(Company company, Pageable pageable);

    @EntityGraph(attributePaths = {"productCategory"})
    Page<Product> findByCompanyAndProductCodeContainingOrderByCreatedAtDesc(Company company,String productCode ,Pageable pageable);

    @EntityGraph(attributePaths = {"productCategory"})
    Page<Product> findByCompanyAndProductNameContainingOrderByCreatedAtDesc(Company company,String productName ,Pageable pageable);

    @EntityGraph(attributePaths = {"productCategory"})
    Page<Product> findByCompanyAndProductCategoryCategoryNameContainingOrderByCreatedAtDesc(Company company,String categoryName ,Pageable pageable);

    List<Product> findByProductCodeContaining(String prodCode);
}
