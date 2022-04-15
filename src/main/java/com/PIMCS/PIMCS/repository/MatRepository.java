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
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatRepository extends JpaRepository<Mat,String> {


     Optional<Mat> findByIdAndCompany(int id, Company company);
     List<Mat> findByCompany(Company company);
     Optional<Mat> findBySerialNumber(String serialNum); //시리얼번호

     @EntityGraph(attributePaths = "product")
     Page<Mat> findByCompanyOrderByIdDesc(Company company, Pageable pageable);

     //검색
     @EntityGraph(attributePaths = "product")
     Page<Mat> findByCompanyAndSerialNumberContaining(Company company, String value, Pageable pageable); //매트버전 및 시리얼번호 검색
     @EntityGraph(attributePaths = "product")
     Page<Mat> findByCompanyAndMatLocationContaining(Company company,String matLocation, Pageable pageable); //매트위치로 검색
     @EntityGraph(attributePaths = "product")
     Page<Mat> findByCompanyAndProductProductCodeContaining(Company company,String productCode, Pageable pageable); //상품코드 검색
     @EntityGraph(attributePaths = "product")
     Page<Mat> findByCompanyAndProductProductNameContaining(Company company,String productName, Pageable pageable); //상품이름 검색

     //end
     @Override
     @EntityGraph(attributePaths = "product")
//     @Query("SELECT m FROM Mat m JOIN FETCH m.product")
     List<Mat> findAll();

     @EntityGraph(attributePaths = "product")
     List<Mat> findByIdIn(List<Integer> idList);

     //임계값 미달 조회
     @Query(
             value = "SELECT m FROM Mat m LEFT JOIN  m.product WHERE m.company=:company AND m.threshold > m.currentInventory",
             countQuery = "SELECT COUNT(m) FROM Mat m WHERE m.company=:company AND m.threshold > m.currentInventory"
     )
     Page<Mat> findMatsBelowThreshold(@Param("company") Company company,Pageable pageable);
}
