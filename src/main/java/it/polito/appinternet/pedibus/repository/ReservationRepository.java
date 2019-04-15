package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
