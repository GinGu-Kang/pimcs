package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatCategoryOrderRepository extends JpaRepository<MatCategoryOrder, Integer> {
    
}
