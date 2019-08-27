package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class Ride {

    @Id
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date rideDate;
    private List<Stop> stops;
    private List<Stop> stopsBack;
    private List<String> companions = new LinkedList<>();
    private List<String> companionsBack = new LinkedList<>();
    boolean confirmed=false;
    boolean confirmedBack=false;

    @JsonCreator
    public Ride(@JsonProperty("flagGoing") boolean flagGoing, @JsonProperty("rideDate") Date rideDate) {
        this.rideDate = rideDate;
    }

}
