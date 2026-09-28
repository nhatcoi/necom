package com.necom;

import com.necom.config.security.UserDetailsImpl;
import com.necom.entity.authentication.Role;
import com.necom.entity.authentication.User;
import com.necom.repository.authentication.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUserByUsername() {
        User user = userRepository.findByUsername("dnucator0").orElseThrow(null);
        Set<Role> roles = user.getRoles();
        UserDetailsImpl test = UserDetailsImpl.build(user);
    }

}
