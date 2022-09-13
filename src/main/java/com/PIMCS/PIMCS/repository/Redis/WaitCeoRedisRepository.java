package com.PIMCS.PIMCS.repository.Redis;

import com.PIMCS.PIMCS.domain.Redis.WaitCeo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
<<<<<<< HEAD:src/main/java/com/PIMCS/PIMCS/repository/Redis/WaitingCeoRedisRepository.java
public interface WaitingCeoRedisRepository extends CrudRepository<WaitingCeo, String> {
    @Override
    List<WaitingCeo> findAll();
=======
public interface WaitCeoRedisRepository extends CrudRepository<WaitCeo, String> {
>>>>>>> 368c2ce87415b60e5d235dda23c3aecfac5c4bf7:src/main/java/com/PIMCS/PIMCS/repository/Redis/WaitCeoRedisRepository.java
}