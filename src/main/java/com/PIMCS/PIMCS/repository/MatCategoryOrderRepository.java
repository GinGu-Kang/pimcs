package com.PIMCS.PIMCS.repository;

import org.springframework.data.jpa.repository.Query;

//@Query("select mco from MatCategoryOrder mco " +
//        "INNER JOIN fetch MatCategory mc on  mco.matCategory = mc " +
//        "INNER JOIN fetch MatOrder mo on mco.matOrder = mo " +
//        "INNER JOIN fetch Company co on mo.company = co " +
//        "where co.id = :companyId")
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.*;
import java.util.List;


public interface MatCategoryOrderRepository extends JpaRepository<MatCategoryOrder, Integer> {


    /**
     *
     * @param companyID
     * @return
     * 회사별 주문 조회
     */
    @Query("select mco,mc,mo,co from MatCategoryOrder mco " +
            "INNER JOIN  MatOrder mo on mco.matOrder = mo " +
            "INNER JOIN  Company co on mo.company = co " +
            "INNER JOIN  MatCategory mc on  mco.matCategory = mc " +
            "where co.id = :companyId")
    List<MatCategoryOrder> findByCompanyId(@Param("companyId") Integer companyID);

}
