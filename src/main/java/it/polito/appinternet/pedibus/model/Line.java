package it.polito.appinternet.pedibus.model;

import lombok.*;
import org.springframework.lang.Nullable;

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
    @ManyToMany(cascade = CascadeType.MERGE)
    private List<Stop> stopListA;

    @Getter @Setter
    @ManyToMany(cascade = CascadeType.MERGE)
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
