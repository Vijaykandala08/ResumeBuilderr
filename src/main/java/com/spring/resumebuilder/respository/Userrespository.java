package com.spring.resumebuilder.respository;

import com.spring.resumebuilder.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface Userrespository extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);
}
