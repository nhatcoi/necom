package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumberAndPassword(String phoneNumber, String password);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
}
