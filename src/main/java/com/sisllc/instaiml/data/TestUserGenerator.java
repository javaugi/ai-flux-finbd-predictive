/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import static com.sisllc.instaiml.data.DataGeneratorBase.FAKER;
import com.sisllc.instaiml.model.TestUser;
import com.sisllc.instaiml.model.User;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Slf4j
public class TestUserGenerator extends DataGeneratorBase {

    public static TestUser generate(String username) {
        return generate(username, new BCryptPasswordEncoder());
    }
    
    public static TestUser generate(String username, PasswordEncoder passwordEncoder) {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        TestUser testUser = TestUser.builder()
                //.id(UUID.randomUUID().toString())
            .name(firstName + " " + lastName)
            .username(username)
            .password(passwordEncoder.encode(username))
            .roles("ROLE_USER,ROLE_ADMIN")
            .email(FAKER.internet().emailAddress())
            .phone(FAKER.phoneNumber().phoneNumber())
            .firstName(firstName)
            .lastName(lastName)
            .age(FAKER.number().numberBetween(18, 70))
            .city(FAKER.address().city())
            .createdDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))            
            .build();
        
        return testUser;
    }
    
    public static TestUser generate(DatabaseClient dbClient, String username, PasswordEncoder passwordEncoder) {
        TestUser testUser = generate(username, passwordEncoder);
        log.trace("testuser {}", insert(dbClient, testUser).subscribe());
        return testUser;
    }
   
    public static Mono<Long> insert(DatabaseClient dbClient, TestUser testUser) {
        return dbClient.sql("""
            INSERT INTO testUsers (id, name, username, password, roles, email, 
                            phone, first_name, last_name, city, age, created_Date, updated_Date
                        ) VALUES (
                            :id, :name, :username, :password, :roles, :email, 
                            :phone, :firstName, :lastName, :city, :age, :createdDate, :updatedDate
                        )
            """)
            .bind("id", testUser.getId())
            .bind("name", testUser.getName())
            .bind("username", testUser.getUsername())
            .bind("password", testUser.getPassword())
            .bind("roles", testUser.getRoles())
            .bind("email", testUser.getEmail())
            .bind("phone", testUser.getPhone())
            .bind("firstName", testUser.getFirstName())
            .bind("lastName", testUser.getLastName())
            .bind("city", testUser.getCity())
            .bind("age", testUser.getAge())
            .bind("createdDate", testUser.getCreatedDate())
            .bind("updatedDate", testUser.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }    
    
}
