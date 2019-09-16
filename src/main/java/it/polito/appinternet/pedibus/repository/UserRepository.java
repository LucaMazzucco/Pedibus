package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    User findByRegistrationNumber(String registrationNumber);
    List<User> findByRolesContains(String role);
    @Override
    List<User> findAll();
}
