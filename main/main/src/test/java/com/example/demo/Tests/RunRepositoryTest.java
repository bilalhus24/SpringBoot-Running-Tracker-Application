package com.example.demo.Tests;

import com.example.demo.entity.Role;
import com.example.demo.entity.Run.Location;
import com.example.demo.entity.Run.Run;
import com.example.demo.entity.User;
import com.example.demo.repository.RunRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("root") // use your datasource username, like defined in app.properties
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RunRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(RunRepositoryTest.class);

    @Autowired
    private RunRepository runRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUserId() {
        User user = new User("John Jenkins", "john.jenkins@gmail.com", "john123");
        userRepository.save(user);
        Long userId = user.getId();

        Run run1 = new Run(6.7, Location.INDOOR, LocalTime.now(), LocalTime.now().plus(1, ChronoUnit.HOURS), userId, LocalDate.now());
        Run run2 = new Run(5.3, Location.OUTDOOR, LocalTime.now(), LocalTime.now().plus(75, ChronoUnit.MINUTES), userId, LocalDate.now());
        runRepository.save(run1);
        runRepository.save(run2);

        List<Run> runs = runRepository.findByUserId(userId);

        assertEquals(2, runs.size());
        assertEquals(userId, runs.get(0).getUserId());
        assertEquals(userId, runs.get(1).getUserId());
        assertEquals(runs.get(0), run1);
        assertEquals(runs.get(1), run2);

        log.info("User: {}", user);
        log.info("Run 1: {}", run1);
        log.info("Run 2: {}", run2);
    }
}
