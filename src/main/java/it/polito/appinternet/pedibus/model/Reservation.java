package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.util.Date;

public class Reservation implements Serializable {
    @Getter @Setter
    @Id
    private long id;

    @Getter @Setter
    private Line line;

    @Getter @Setter
    @Nullable
    private Stop departure;

    @Getter @Setter
    @Nullable
    private Stop arrival;

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
