package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class Stop {
    private String stopName;
    private long time;
    private List<String> reservations = new LinkedList<>();

    @JsonCreator
    public Stop(@JsonProperty("stopName") String stopName, @JsonProperty("time") long time) {
        this.time = time;
        this.stopName = stopName;
    }
}
