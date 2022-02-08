package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.MatOrder;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MatOrderRepository extends JpaRepository<MatOrder,Integer> {
}
