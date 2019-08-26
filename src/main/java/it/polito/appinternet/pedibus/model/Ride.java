package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class Ride {

    @Id
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date rideDate;
    private boolean flagGoing;
    private List<Stop> stops;

    @JsonCreator
    public Ride(@JsonProperty("flagGoing") boolean flagGoing, @JsonProperty("rideDate") Date rideDate) {
        this.flagGoing = flagGoing;
        this.rideDate = rideDate;
    }

}
