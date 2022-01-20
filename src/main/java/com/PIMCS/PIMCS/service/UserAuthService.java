package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.LoginForm;
import com.PIMCS.PIMCS.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<User> findUser(String email){
        Optional<User> findUser = userAuthRepository.findByEmail(email);
        return findUser;
    }
    public void userDelete(String email){
        userAuthRepository.deleteByEmail(email);

    }
    public String userUpdate(User user){
        userAuthRepository.save(user);

        return user.getEmail();
    }
    public String userLogin(LoginForm loginForm){
        Optional<User> user= userAuthRepository.findByEmail(loginForm.getEmail());


        if(!user.isEmpty()){
            return user.get().getEmail();
        }else{
            return "empty";
        }



    }
}
