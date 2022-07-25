package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.domain.SendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SendHistoryRepository extends JpaRepository<SendHistory,Integer> {
}
