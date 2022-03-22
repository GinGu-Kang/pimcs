package com.PIMCS.PIMCS.Repository.Redis;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Redis.WaitingCeo;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.Redis.WaitingCeoRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class waitingCeoRedisRepositoryTest {
    @Autowired
    private WaitingCeoRedisRepository waitingCeoRedisRepository;

    @Test
    void test() {
        User user = User.builder().email("hi").name("찡꾸").build();
        Company company = Company.builder().companyName("나는바보코퍼레이션").build();
        WaitingCeo waitingCeo = WaitingCeo.builder().user(user).company(company).build();


        // 저장
//        waitingCeoRedisRepository.save(waitingCeo);

        // `keyspace:id` 값을 가져옴

//        System.out.println(waitingCeo.getUser().getName());
//        System.out.println(waitingCeo.getCompany().getCompanyName());
//        System.out.println(waitingCeo.getId());
        System.out.println(waitingCeoRedisRepository.findById("34c5b86d-6a77-4086-8cf9-34bcecea066d").get().getUser().getName());
        System.out.println(waitingCeoRedisRepository.findById("34c5b86d-6a77-4086-8cf9-34bcecea066d").get().getCompany().getCompanyName());

//        System.out.println(waitingUserRedisRepository.findById("57fa959f-f4f5-405a-a153-527f3e146f7d").get().getUser().getName());
//        System.out.println(repo.findById("5c02065d-3328-4cf6-9898-c5238ca18eee").get().getAge());

        // Person Entity 의 @RedisHash 에 정의되어 있는 keyspace (people) 에 속한 키의 갯수를 구함
//        waitingUserRedisRepository.delete(waitingUserRedisRepository.findById("b7e5f455-31ee-4661-90ee-5e5967d9a52f").get());
//        System.out.println(waitingCeoRedisRepository.count());

        // 삭제
    }

}