package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
public class Reservation implements Serializable {
    @Getter @Setter
    @Id
    private long id;

    @Getter @Setter
    private Line line;

    @Getter @Setter
    private Stop departure;

    @Getter @Setter
    private Stop arrival;

    @Getter @Setter
    private Person passenger;

    @Getter @Setter
    private Date reservation_date;

}
