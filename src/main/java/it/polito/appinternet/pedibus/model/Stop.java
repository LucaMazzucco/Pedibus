package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class Stop {
    private String stopName;
    private long time;
    private List<String> reservations = new LinkedList<>();
    private GeoJsonPoint location;

    @JsonCreator
    public Stop(@JsonProperty("stopName") String stopName, @JsonProperty("time") long time,
                @JsonProperty("Lat") double locationLat, @JsonProperty("Long") double locationLong) {
        this.time = time;
        this.stopName = stopName;
        this.location = new GeoJsonPoint(locationLat,locationLong);
    }
}
