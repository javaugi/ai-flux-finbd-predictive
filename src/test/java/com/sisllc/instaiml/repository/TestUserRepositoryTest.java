/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.TestPostgresConfig;
import com.sisllc.instaiml.data.TestUserGenerator;
import com.sisllc.instaiml.model.TestUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestPostgresConfig.class)
@ActiveProfiles("test")
public class TestUserRepositoryTest {

    private static final String FAKE_USERNAME = new net.datafaker.Faker().internet().username();

    @Autowired
    private TestUserRepository testUserRepository;

    @BeforeAll
    public static void init(@Autowired TestUserRepository testUserRepository) {
        // Optional: clear data once before all tests start
        testUserRepository.deleteAll().block();
    }

    @AfterEach
    public void cleanup() {
        // Clean up after each test to avoid side effects
        testUserRepository.deleteAll().block();
    }

    /*
    Next step for your failure
        I recommend:
            Keep the generic cleanup above
            Modify UserGenerator.generate() so id is null
            Let save() perform an insert, not an update
            That should make your saveAndRetrieveUser() pass consistently.
     */
    @Test
    void saveAndRetrieveUser() {
        TestUser testuser = TestUserGenerator.generate(FAKE_USERNAME);

        testUserRepository.save(testuser)
                .as(StepVerifier::create)
            .expectNextMatches(savedUser -> savedUser.getId() != null)
            .verifyComplete();

        testUserRepository.findAll()
                .as(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete();
    }
}


/*
    @Autowired
    private DatabaseClient databaseClient;

    @AfterEach
    void cleanup() {
        databaseClient.sql("TRUNCATE TABLE users RESTART IDENTITY CASCADE").then().block();
    }
 */
