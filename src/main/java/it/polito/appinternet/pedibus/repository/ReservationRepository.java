package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Line;
import it.polito.appinternet.pedibus.model.Person;
import it.polito.appinternet.pedibus.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //Reservation findByIdAndLineAndReservation_date (Reservation reservation, Line line, Date reservation_date);
    List<Reservation> findAll();

    //List<Reservation> findByLineAndReservationDate(long a, Date b);
    List<Reservation> findByLine_LineNameAndReservationDate(String name,Date date);
}
