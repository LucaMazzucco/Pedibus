package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Shift;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends MongoRepository<Shift, String> {
    Optional<Shift> findById(String id);
    List<Shift> findByEmail(String email);
    Shift findByEmailAndLineNameAndFlagGoingAndConfirmed1IsFalseAndConfirmed2IsFalse(String email, String lineName, boolean flagGoing);
    List<Shift> findByEmailAndLineNameAndFlagGoing(String email, String lineName, boolean flagGoing);
    Shift findByEmailAndLineNameAndRideDateAndFlagGoing(String email, String lineName, long rideDate, boolean flagGoing);
    Shift findShiftById(String id);
}
