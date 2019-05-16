package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "lines")
@NoArgsConstructor
public class Line{

    @Id
    @Getter @Setter
    private String id;

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    private List<Stop> stopListA;

    @Getter @Setter
    private List<Stop> stopListR;

    private List<String> lineAdmins = new ArrayList<>();

    @JsonCreator
    public Line(@JsonProperty("lineName") String lineName, @JsonProperty("stopListA") List<Stop> stopListA, @JsonProperty("stopListR") List<Stop> stopListR) {
        this.lineName = lineName;
        this.stopListA = stopListA;
        this.stopListR = stopListR;
    }

    @Override
    public String toString() {
        return lineName + stopListA + stopListR;
    }

    public List<String> getAdmins(){
        return lineAdmins;
    }
}
