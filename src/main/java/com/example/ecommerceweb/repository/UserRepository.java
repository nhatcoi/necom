package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumberAndPassword(String phoneNumber, String password);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String username);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    @NotNull Page<User> findAll(@NotNull Pageable pageable);

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Long findIdByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%")
    Page<User> searchUsers(Pageable pageable, @Param("keyword") String keyword);
}
