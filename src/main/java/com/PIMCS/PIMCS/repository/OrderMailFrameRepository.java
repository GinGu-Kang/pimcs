package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.OrderMailFrame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMailFrameRepository extends JpaRepository<OrderMailFrame,Integer> {
    OrderMailFrame getOne(Integer id);
}
