package com.PIMCS.PIMCS.Repository.Redis;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitingCeo;
import com.PIMCS.PIMCS.domain.Redis.WaitingUser;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.Redis.WaitingCeoRedisRepository;
import com.PIMCS.PIMCS.repository.Redis.WaitingUserRedisRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisRepositoryTest {

    @Autowired
    private WaitingUserRedisRepository waitingUserRedisRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void test() {
        User user = userRepository.findByEmail("rkdwlsrn212@gmail.com").get();
//        user.setCompany(null);
//        user.setUserRoles(null);
        WaitingUser waitingUser = WaitingUser.builder().user(user).build();


        // 저장
        waitingUserRedisRepository.save(waitingUser);

        // `keyspace:id` 값을 가져옴

        System.out.println(waitingUser.getUser().getName());
        System.out.println(waitingUser.getId());
//        System.out.println(waitingUserRedisRepository.findById("57fa959f-f4f5-405a-a153-527f3e146f7d").get().getUser().getName());
//        System.out.println(repo.findById("5c02065d-3328-4cf6-9898-c5238ca18eee").get().getAge());

        // Person Entity 의 @RedisHash 에 정의되어 있는 keyspace (people) 에 속한 키의 갯수를 구함
//        waitingUserRedisRepository.delete(waitingUserRedisRepository.findById("b7e5f455-31ee-4661-90ee-5e5967d9a52f").get());
        System.out.println(waitingUserRedisRepository.count());

        // 삭제
    }
}