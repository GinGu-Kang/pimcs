package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.domain.SendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendHistoryRepository extends JpaRepository<SendHistory,Integer> {
}
