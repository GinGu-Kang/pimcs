package com.PIMCS.PIMCS.repository.Redis;

import com.PIMCS.PIMCS.domain.Redis.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceRepository   extends CrudRepository<Device, String> {

}
