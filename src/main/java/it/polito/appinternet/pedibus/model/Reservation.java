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
@Getter @Setter
public class Reservation {

    @Id
    private String id;

    private String lineName;

    private String stopName;

    private User passenger;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date reservationDate;

    //true = da stopName a scuola; false = da scuola a stopName
    private boolean flagAndata;

    private boolean isPresent;

    public Reservation(@JsonProperty("lineName") String lineName, @JsonProperty("stopName") String stopName,
                       @JsonProperty("passenger") User passenger, @JsonProperty("reservationDate") Date reservationDate,
                       @JsonProperty("isA") boolean flagAndata, @JsonProperty("isPresent") Boolean isPresent) {
        this.lineName = lineName;
        this.stopName = stopName;
        this.passenger = passenger;
        this.reservationDate = reservationDate;
        this.flagAndata = flagAndata;
        this.isPresent = isPresent;
    }
}
