package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findByEmail(String email);
}
