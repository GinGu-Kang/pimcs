package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.domain.OwnDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnDeviceRepository extends JpaRepository<OwnDevice,Integer> {
    List<OwnDevice> findAllBySerialNumberIn(List<String> deviceSerialList);

}
