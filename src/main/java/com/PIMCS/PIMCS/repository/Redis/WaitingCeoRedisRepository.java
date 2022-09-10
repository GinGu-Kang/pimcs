package com.PIMCS.PIMCS.repository.Redis;

import com.PIMCS.PIMCS.domain.Redis.WaitingCeo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingCeoRedisRepository extends CrudRepository<WaitingCeo, String> {
    @Override
    List<WaitingCeo> findAll();
}