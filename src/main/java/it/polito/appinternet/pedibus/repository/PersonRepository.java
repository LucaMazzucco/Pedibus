package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Line;
import it.polito.appinternet.pedibus.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findAll();
    Person findByRegistrationNumber(String rn);
}
