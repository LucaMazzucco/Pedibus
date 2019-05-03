package it.polito.appinternet.pedibus.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.List;

public class Line implements Serializable {
    @Id
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    private List<Stop> stopListA;

    @Getter @Setter
    private List<Stop> stopListR;

    public Line(String lineName, List<Stop> stopListA, List<Stop> stopListR) {
        this.lineName = lineName;
        this.stopListA = stopListA;
        this.stopListR = stopListR;
    }

    @Override
    public String toString() {
        return lineName + stopListA + stopListR;
    }
}
