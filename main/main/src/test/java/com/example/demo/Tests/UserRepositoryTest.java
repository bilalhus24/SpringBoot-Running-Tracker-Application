package com.example.demo.Tests;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("root")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExistsById() {
        User user = new User("John Jenkins", "john.jenkins@gmail.com", "john123", Role.ROLE_GUEST);
        userRepository.save(user);
        Long userId = user.getId();

        boolean shouldBeTrue = userRepository.existsById(userId);
        boolean shouldBeFalse = userRepository.existsById(userId + 1);

        assertTrue(shouldBeTrue);
        log.info("Does user with user id " + userId + " exist? " + (shouldBeTrue ? "yes" : "no"));
        assertFalse(shouldBeFalse);
        log.info("Does user with user id " + (userId + 1) + " exist? " + (shouldBeFalse ? "yes" : "no"));
    }

    @Test
    void testFindByEmail() {
        User user = new User("John Jenkins", "john.jenkins@gmail.com", "john123", Role.ROLE_GUEST);
        userRepository.save(user);
        String email = user.getEmail();

        Optional<User> foundUser = userRepository.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
        log.info("Original user: {}", user);
        log.info("Found user: {}", foundUser);
        log.info("Original email: {}", email);
        log.info("Found email: {}", foundUser.get().getEmail());
    }
}
