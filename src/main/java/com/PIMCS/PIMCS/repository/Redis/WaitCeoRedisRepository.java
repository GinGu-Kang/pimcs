package com.PIMCS.PIMCS.repository.Redis;

import com.PIMCS.PIMCS.domain.Redis.WaitCeo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitCeoRedisRepository extends CrudRepository<WaitCeo, String> {
    @Override
    List<WaitCeo> findAll();
}