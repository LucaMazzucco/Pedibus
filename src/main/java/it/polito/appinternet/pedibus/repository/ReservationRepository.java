package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, Long> {
    //Reservation findByIdAndLineAndReservation_date (Reservation reservation, Line lineName, Date reservation_date);
    List<Reservation> findAll();
    //List<Reservation> findByLineAndReservationDate(String a, Date b);
    List<Reservation> findByLineNameAndReservationDateAndFlagAndataIsTrue(String a, Date b);
    List<Reservation> findByLineNameAndReservationDateAndFlagAndataIsFalse(String a, Date b);

    //List<Reservation> findByLine_LineNameAndReservationDate(String name,Date date);
}
