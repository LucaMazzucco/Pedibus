package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String surname;

    @Getter @Setter
    @Column(unique = true)
    private String registrationNumber;

    public Person(String name, String surname, String ssn){
        this.name = name;
        this.surname = surname;
        this.registrationNumber = ssn;
    }
}
