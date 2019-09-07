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
    List<Reservation> findByChild(String childId);
    //List<Reservation> findByLineAndReservationDate(String a, Date b);
    List<Reservation> findByLineNameAndReservationDateAndFlagGoingIsTrue(String a, long b);
    List<Reservation> findByLineNameAndReservationDateAndFlagGoingIsFalse(String a, long b);
    List<Reservation> findByLineNameAndReservationDateAndFlagGoing(String lineName,long rDate,boolean flag);
    Reservation findById(String id);
    Reservation findByIdAndLineNameAndReservationDate(String id, String lineName, long date);
    Reservation findByLineNameAndReservationDateAndFlagGoingAndChild(String lineName, long rDate, boolean flag, String childId);
    //Reservation findAllBy_Id
    //List<Reservation> findByLine_LineNameAndReservationDate(String name,Date rideDate);
}
