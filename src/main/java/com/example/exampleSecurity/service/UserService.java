package com.example.exampleSecurity.service;

import com.example.exampleSecurity.entity.User;
import com.example.exampleSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public UserDetails loadUserByusername(String username){
        return userDetailsService.loadUserByUsername(username);
    }

    public void registerUser(User user){
        if (!userRepository.existsByUsername(user.getUsername())) {
            userRepository.save(user);
        }else {
            throw new RuntimeException("User already exists");
        }
    }
}
