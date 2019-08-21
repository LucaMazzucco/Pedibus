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
    List<Reservation> findByLineNameAndReservationDateAndFlagGoingIsTrue(String a, Date b);
    List<Reservation> findByLineNameAndReservationDateAndFlagGoingIsFalse(String a, Date b);
    List<Reservation> findByLineNameAndReservationDateAndFlagGoing(String lineName,Date rDate,boolean flag);
    Reservation findById(String id);
    Reservation findByIdAndLineNameAndReservationDate(String id, String lineName, Date date);
    Reservation findByLineNameAndReservationDateAndFlagGoingAndChild(String lineName, Date rDate, boolean flag, String childId);
    //Reservation findAllBy_Id
    //List<Reservation> findByLine_LineNameAndReservationDate(String name,Date rideDate);
}
