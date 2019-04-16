package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
public class Reservation implements Serializable {
    @Getter @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @Getter @Setter
    private Line line;

    @OneToOne(cascade = CascadeType.MERGE)
    @Getter @Setter
    @Nullable
    private Stop departure;

    @OneToOne(cascade = CascadeType.MERGE)
    @Getter @Setter
    @Nullable
    private Stop arrival;

    @OneToOne(cascade = CascadeType.MERGE)
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
