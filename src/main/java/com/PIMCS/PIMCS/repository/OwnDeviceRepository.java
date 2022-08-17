package com.PIMCS.PIMCS.repository;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.OrderMailFrame;
import com.PIMCS.PIMCS.domain.OwnDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnDeviceRepository extends JpaRepository<OwnDevice,Integer> {
    List<OwnDevice> findAllBySerialNumberIn(List<String> deviceSerialList);
    List<OwnDevice> findByCompany(Company company);
    Optional<OwnDevice> findBySerialNumber(String serialNumber);
    Optional<OwnDevice> findByCompanyAndSerialNumber(Company company, String serialNumber);
    void deleteAllByIdIn(List<Integer> ownDeviceList);

}
