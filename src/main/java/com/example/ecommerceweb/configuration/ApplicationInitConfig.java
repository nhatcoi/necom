package com.example.ecommerceweb.configuration;

import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j // annotation to enable logging
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByPhoneNumber("123456").isEmpty()) {
                var admin = new com.example.ecommerceweb.entity.User();
                admin.setPhoneNumber("123456");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.builder().id(RoleEnum.ADMIN.getValue()).build());
                userRepository.save(admin);
                log.warn("Admin user created with phone number 123456 and password admin");
            }
        };
    }
}
