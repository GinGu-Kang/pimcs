package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatOrderRepository extends JpaRepository<MatOrder,Integer> {
    List<MatOrder> findByCompany(Company company);
    Page<MatOrder> findAll(Pageable pageable);
    Page<MatOrder> findByDepositerNameLikeAndTotalPriceBetween(String depositerName,Integer totalPriceStart,Integer totalPriceEnd,Pageable pageable);
    MatOrder getOne(Integer id);
}
