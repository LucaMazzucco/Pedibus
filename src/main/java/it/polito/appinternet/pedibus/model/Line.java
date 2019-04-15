package it.polito.appinternet.pedibus.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
public class Line implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long lineId;

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    @ElementCollection
    private List<String> stopListA;

    @Getter @Setter
    @ElementCollection
    private List<String> stopListR;

    public Line(String lineName, List<String> stopListA, List<String> stopListR) {
        this.lineName = lineName;
        this.stopListA = stopListA;
        this.stopListR = stopListR;
    }

    @Override
    public String toString() {
        return lineName + stopListA + stopListR;
    }
}
