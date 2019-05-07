package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "people")
@NoArgsConstructor
public class Person {

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
