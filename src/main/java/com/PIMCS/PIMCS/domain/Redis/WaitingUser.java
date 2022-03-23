package com.PIMCS.PIMCS.domain.Redis;

import com.PIMCS.PIMCS.domain.User;
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
@RedisHash(value = "waitingUser", timeToLive = 600)
public class WaitingUser {
    @Id
    private String id;
    private User user;
}
