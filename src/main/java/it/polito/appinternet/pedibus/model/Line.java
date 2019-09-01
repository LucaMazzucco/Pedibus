package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Document(collection = "lines")
@NoArgsConstructor
@Getter @Setter
public class Line{
    @Id
    private String id;
    private String lineName;
    private List<String> lineAdmins = new ArrayList<>();
    private List<Ride> rides = new LinkedList<>();

//    @JsonCreator
//    public Line(@JsonProperty("lineName") String lineName, @JsonProperty("stopListA") List<Stop> stopListA, @JsonProperty("stopListR") List<Stop> stopListR) {
//        this.lineName = lineName;
//        this.stopListA = stopListA;
//        this.stopListR = stopListR;
//    }

//    @Override
//    public String toString() {
//        return lineName + stopListA + stopListR;
//    }

    public Line(@JsonProperty("lineName") String lineName) {
        this.lineName = lineName;
    }
    public List<String> getAdmins(){
        return lineAdmins;
    }
}
