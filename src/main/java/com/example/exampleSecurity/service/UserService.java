package com.example.exampleSecurity.service;

import com.example.exampleSecurity.entity.User;
import com.example.exampleSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void registerUser(User user){
        if (!userRepository.existsByUsername(user.getUsername())) {
            userRepository.save(user);
        }else {
            throw new RuntimeException("User already exists");
        }
    }
}
