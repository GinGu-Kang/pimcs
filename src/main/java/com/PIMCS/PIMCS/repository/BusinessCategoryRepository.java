package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.BusinessCategory;
import com.PIMCS.PIMCS.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessCategoryRepository extends JpaRepository<BusinessCategory,Integer> {
}
