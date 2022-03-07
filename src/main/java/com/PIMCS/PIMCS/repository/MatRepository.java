package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MatRepository extends JpaRepository<Mat,String> {


     Optional<Mat> findByIdAndCompany(int id, Company company);

     List<Mat> findByCompany(Company company);
     Optional<Mat> findBySerialNumber(String serialNum); //시리얼번호로 검색

     @EntityGraph(attributePaths = "product")
     Page<Mat> findByCompanyOrderByIdDesc(Company company, Pageable pageable);

     @EntityGraph(attributePaths = "product")
     List<Mat> findByMatLocationContaining(String matLocation); //매트위치로 검색

     @EntityGraph(attributePaths = "product")
     List<Mat> findBySerialNumberContaining(String value); //매트버전 검색

     @Override
     @EntityGraph(attributePaths = "product")
//     @Query("SELECT m FROM Mat m JOIN FETCH m.product")
     List<Mat> findAll();

}
