package com.PIMCS.PIMCS.repository.Redis;

import com.PIMCS.PIMCS.domain.Redis.WaitingCeo;
import org.springframework.data.repository.CrudRepository;

public interface WaitingCeoRedisRepository extends CrudRepository<WaitingCeo, String> {
}