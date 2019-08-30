package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface RideRepository extends MongoRepository<Ride, Long> {
    List<Ride> findAll();
    Ride findById(String id);
    Ride findByRideDate(Date d);
    Ride findByRideDateAndFlagGoing(Date d,Boolean b);
    List<Ride> findByCompanionsContaining(String companion);
}
