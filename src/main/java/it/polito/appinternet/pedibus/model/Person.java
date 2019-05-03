package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class Person implements Serializable {

    @Id
    @Getter @Setter
    private long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String surname;

    @Getter @Setter
    private String registrationNumber;

    public Person(String name, String surname, String ssn){
        this.name = name;
        this.surname = surname;
        this.registrationNumber = ssn;
    }

    @Override
    public String toString() {
        return registrationNumber + " " +  name +  " " + surname;
    }
}
