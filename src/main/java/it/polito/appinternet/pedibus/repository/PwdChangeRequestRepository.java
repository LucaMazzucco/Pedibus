package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.PwdChangeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PwdChangeRequestRepository extends MongoRepository<PwdChangeRequest, String> {
    PwdChangeRequest findByToken(String token);
}
