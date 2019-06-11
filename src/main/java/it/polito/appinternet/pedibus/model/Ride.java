package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "rides")
@NoArgsConstructor
@Getter @Setter
public class Ride {

    @Id
    private String id;

    private Boolean flagAndata;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date rideDate;

    private List<String> reservations;

    @JsonCreator
    public Ride(@JsonProperty("flagAndata") Boolean flagAndata, @JsonProperty("rideDate") Date rideDate, @JsonProperty("reservations") List<String> reservations) {
        this.flagAndata = flagAndata;
        this.rideDate = rideDate;
        this.reservations = reservations;
    }

}
