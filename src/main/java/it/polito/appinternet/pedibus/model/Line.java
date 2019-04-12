package it.polito.appinternet.pedibus.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
public class Line implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long lineId;

    @Getter @Setter
    private String lineName;

    public Line(String name) {
        this.lineName = name;
    }
}
