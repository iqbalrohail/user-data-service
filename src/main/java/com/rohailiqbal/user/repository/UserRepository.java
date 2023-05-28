package com.rohailiqbal.user.repository;

import com.rohailiqbal.user.domain.UserDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The UserRepository interface is a repository for UserDomain objects.
 * It extends the MongoRepository interface and provides additional methods to retrieve user information from the database.
 * This interface is designed to be used to interact with the database and retrieve user information.
 */
@Repository
public interface UserRepository extends MongoRepository<UserDomain, String> {
    UserDomain findByUsername(String username);
}