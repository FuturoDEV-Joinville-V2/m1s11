package com.example.exampleSecurity.controller;

import com.example.exampleSecurity.config.security.JwtUtil;
import com.example.exampleSecurity.dtos.AuthRequest;
import com.example.exampleSecurity.dtos.AuthResponse;
import com.example.exampleSecurity.service.UserDetailsServiceImpl;
import com.example.exampleSecurity.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest){
        try{
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());

            if(userService.validatePassword(authRequest.password(), userDetails.getPassword())){
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername()));
            }else{
                return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
            }
        }catch(UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }
}
