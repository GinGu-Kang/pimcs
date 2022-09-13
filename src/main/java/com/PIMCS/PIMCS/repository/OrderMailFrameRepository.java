package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.OrderMailFrame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderMailFrameRepository extends JpaRepository<OrderMailFrame,Integer> {
    OrderMailFrame getOne(Integer id);
    Optional<OrderMailFrame> findByGreeting(String greeting);
}
