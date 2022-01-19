package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Mat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatRepository extends JpaRepository<Mat,String> {

     Optional<Mat> findBySerialNumber(String serialNum); //시리얼번호로 검색

     List<Mat> findByMatLocationContaining(String matLocation); //매트위치로 검색

     List<Mat> findBySerialNumberContaining(String value); //매트버전 검색

}
