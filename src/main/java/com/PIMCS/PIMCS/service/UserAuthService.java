package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {
    private  final  UserAuthRepository userAuthRepository;

    @Autowired
    public UserAuthService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    public String signUp(User user){
        userAuthRepository.save(user);
        return user.getEmail();
    }
}
