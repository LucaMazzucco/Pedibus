package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reservations")
@NoArgsConstructor
public class Reservation {

    @Id
    @Getter @Setter
    private String id;

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    private String stopName;

    @Getter @Setter
    private Person passenger;

    @Getter @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date reservationDate;

    //true = da stopName a scuola; false = da scuola a stopName
    @Getter @Setter
    private boolean flagAndata;

    public Reservation(@JsonProperty("lineName") String lineName, @JsonProperty("stopName") String stopName, @JsonProperty("passenger") Person passenger, @JsonProperty("reservationDate") Date reservationDate, @JsonProperty("isA") boolean flagAndata) {
        this.lineName = lineName;
        this.stopName = stopName;
        this.passenger = passenger;
        this.reservationDate = reservationDate;
        this.flagAndata = flagAndata;
    }
}
