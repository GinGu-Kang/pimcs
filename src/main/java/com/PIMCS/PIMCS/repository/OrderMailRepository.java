package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.OrderMail;
import com.PIMCS.PIMCS.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMailRepository extends JpaRepository<OrderMail,Integer> {
    OrderMail getOne(Integer id);
}
