package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, Long> {
    //Reservation findByIdAndLineAndReservation_date (Reservation reservation, Line line, Date reservation_date);
    List<Reservation> findAll();

    //List<Reservation> findByLineAndReservationDate(long a, Date b);
    List<Reservation> findByLine_LineNameAndReservationDate(String name,Date date);

}
