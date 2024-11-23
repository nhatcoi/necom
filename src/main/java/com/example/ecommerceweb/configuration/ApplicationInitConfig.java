package com.example.ecommerceweb.configuration;

import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j // annotation to enable logging
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByPhoneNumber(adminUsername).isEmpty()) {
                var admin = new com.example.ecommerceweb.entity.User();
                admin.setPhoneNumber(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.builder().id(RoleEnum.ADMIN.getValue()).build());
                userRepository.save(admin);
                log.warn("Admin user created");
            }
        };
    }
}
