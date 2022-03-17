package com.PIMCS.PIMCS.Repository;

import com.PIMCS.PIMCS.domain.Redis.Person;
import com.PIMCS.PIMCS.repository.Redis.PersonRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisRepositoryTest {

    @Autowired
    private PersonRedisRepository repo;

    @Test
    void test() {
        Person person = new Person("Park", 20);

        // 저장
        repo.save(person);

        // `keyspace:id` 값을 가져옴

        System.out.println("가져와라");
        System.out.println(person.getId());
//        System.out.println(repo.findById("5c02065d-3328-4cf6-9898-c5238ca18eee").get().getAge());

        // Person Entity 의 @RedisHash 에 정의되어 있는 keyspace (people) 에 속한 키의 갯수를 구함
        repo.count();

        // 삭제
//        repo.delete(person);
    }
}