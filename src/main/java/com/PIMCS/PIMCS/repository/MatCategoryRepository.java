package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatCategoryRepository extends JpaRepository<MatCategory,Integer> {

    Optional<MatCategory> findByMatCategoryName(String matCategoryName);
}
