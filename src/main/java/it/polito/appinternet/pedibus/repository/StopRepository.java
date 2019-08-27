package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopRepository extends MongoRepository<Stop, String> {
}
