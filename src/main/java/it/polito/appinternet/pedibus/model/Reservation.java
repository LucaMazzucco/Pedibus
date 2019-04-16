package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
public class Reservation implements Serializable {
    @Getter @Setter
    @Id
    private long id;

    @ManyToOne
    @Getter @Setter
    private Line line;

    @ManyToOne
    @Getter @Setter
    private Stop departure;

    @ManyToOne
    @Getter @Setter
    private Stop arrival;

    @ManyToOne
    @Getter @Setter
    private Person passenger;

    @Getter @Setter
    private Date reservationDate;

    @Getter @Setter
    private boolean back;

    public Reservation(Line line, Stop departure, Stop arrival, Person passenger, Date reservationDate, boolean back) {
        this.line = line;
        this.departure = departure;
        this.arrival = arrival;
        this.passenger = passenger;
        this.reservationDate = reservationDate;
        this.back = back;
    }

}
