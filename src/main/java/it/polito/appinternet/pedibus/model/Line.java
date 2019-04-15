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
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    @OneToMany(cascade = CascadeType.ALL)
    private List<Stop> stopListA;

    @Getter @Setter
    @OneToMany(cascade = CascadeType.ALL)
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
