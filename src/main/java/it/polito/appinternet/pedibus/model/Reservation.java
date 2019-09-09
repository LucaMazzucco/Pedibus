package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reservations")
@NoArgsConstructor
@Getter @Setter
public class Reservation {

    @Id
    private String id;

    private String lineName;
    private String stopName;
    private String child;
    private String parent;
    private long reservationDate; //Non mandare a backend
    //true = da stopName a scuola; false = da scuola a stopName
    private boolean flagGoing;
    private boolean isPresent;

    public Reservation(String lineName, String stopName, String child, String parent, long reservationDate, boolean flagGoing, boolean isPresent) {
        this.lineName = lineName;
        this.stopName = stopName;
        this.child = child;
        this.parent = parent;
        this.reservationDate = reservationDate;
        this.flagGoing = flagGoing;
        this.isPresent = isPresent;
    }
//    public Reservation(@JsonProperty("lineName") String lineName, @JsonProperty("stopName") String stopName,
//                       @JsonProperty("passenger") User passenger, @JsonProperty("reservationDate") Date reservationDate,
//                       @JsonProperty("isA") boolean flagGoing, @JsonProperty("isPresent") Boolean isPresent) {
//        this.lineName = lineName;
//        this.stopName = stopName;
//        this.passenger = passenger;
//        this.reservationDate = reservationDate;
//        this.flagGoing = flagGoing;
//        this.isPresent = isPresent;
//    }
}
