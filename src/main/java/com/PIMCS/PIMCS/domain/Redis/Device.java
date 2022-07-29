package com.PIMCS.PIMCS.domain.Redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "device")
public class Device {
    @Id
    private String serialNumber;
    private int inventoryWeight;

    public Device(String serialNumber, int inventoryWeight){
        this.serialNumber = serialNumber;
        this.inventoryWeight = inventoryWeight;
    }
}
