package it.polito.appinternet.pedibus.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Stop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String stopName;


    public Stop(String stopName) {
        this.stopName = stopName;
    }
}
