package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends MongoRepository<Person, Long> {
    List<Person> findAll();
    Person findByRegistrationNumber(String rn);
}
