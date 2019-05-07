package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reservations")
@NoArgsConstructor
public class Reservation {

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    private String stopName;

    @Getter @Setter
    private Person passenger;

    @Getter @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date reservationDate;

    @Getter @Setter
    private boolean isA;

    public Reservation(@JsonProperty("lineName") String lineName, @JsonProperty("stopName") String stopName, @JsonProperty("passenger") Person passenger, @JsonProperty("reservationDate") Date reservationDate, @JsonProperty("isA") boolean isA) {
        this.lineName = lineName;
        this.stopName = stopName;
        this.passenger = passenger;
        this.reservationDate = reservationDate;
        this.isA = isA;
    }
}
