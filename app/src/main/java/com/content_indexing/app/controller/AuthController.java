package com.content_indexing.app.controller;

import com.content_indexing.app.dto.TokenDto;
import com.content_indexing.app.security.JwtUtils;
import com.content_indexing.app.entity.UserEntity;
import com.content_indexing.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    @PostMapping("/register")

    public ResponseEntity<?> register(@RequestBody UserEntity user){
               if(userRepository.findByUsername(user.getUsername()).isPresent()){
                   return ResponseEntity.badRequest().body("Username already exists");
               }
               user.setPassword(passwordEncoder.encode(user.getPassword()));
               userRepository.save(user);
               return ResponseEntity.ok().build();
    }
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserEntity credentials){
        UserEntity user = userRepository.findByUsername(credentials.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            String token = jwtUtils.generateToken(user.getUsername());
            return ResponseEntity.ok(new TokenDto(token));
        }
        return ResponseEntity.status(401).build();
    }
}

