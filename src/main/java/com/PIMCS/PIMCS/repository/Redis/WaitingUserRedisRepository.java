package com.PIMCS.PIMCS.repository.Redis;

import com.PIMCS.PIMCS.domain.Redis.WaitingUser;
import org.springframework.data.repository.CrudRepository;

public interface WaitingUserRedisRepository extends CrudRepository<WaitingUser, String> {
}
