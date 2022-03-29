package com.PIMCS.PIMCS.domain.Redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RedisHash(value = "findPwd", timeToLive = 600)
public class FindPwdVO {
    @Id
    private String id;
    private String email;
}
