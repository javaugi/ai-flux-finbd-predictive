package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.model.TestUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TestUserRepository extends ReactiveCrudRepository<TestUser, String> {
    // Return a Flux of string IDs
    @Query("SELECT id FROM testUser")
    Flux<String> getUserIds();

    @Query("SELECT * FROM testUser")
    Flux<TestUser> getAllUsers();

    Mono<TestUser> findByUsername(String username);
}
