package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Child;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends MongoRepository<Child,Long> {
    List<Child> findAll();
    Child findByRegistrationNumber(String registrationNumber);
    Child findById(String id);
    List<Child> findByParentId(String parentId);
}
