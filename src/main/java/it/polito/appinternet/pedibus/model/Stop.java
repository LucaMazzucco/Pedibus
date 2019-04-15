package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Stop {
    @Id
    @ManyToOne
    @JoinColumn(name = "line_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String stopName;

    public Stop(String stopName) {
        this.stopName = stopName;
    }
}
