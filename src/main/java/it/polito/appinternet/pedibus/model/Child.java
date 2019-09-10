package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "children")
@NoArgsConstructor
@Getter @Setter
public class Child {
    @Id
    private String id;

    private String name,surname,registrationNumber;
    private String parentId;
    private String defaultLine, defaultStop;

    public Child(String name, String surname, String registrationNumber, String parentId) {
        this.name = name;
        this.surname = surname;
        this.registrationNumber = registrationNumber;
        this.parentId = parentId;
        this.id = "";
        this.defaultStop = "";
        this.defaultLine = "";
    }
    public Child(String name, String surname, String registrationNumber, String parentId,
                 String defaultLine, String defaultStop) {
        this.name = name;
        this.surname = surname;
        this.registrationNumber = registrationNumber;
        this.parentId = parentId;
        this.id = "";
        this.defaultLine = defaultLine;
        this.defaultStop = defaultStop;
    }
//    @JsonCreator
//    public Passenger(@JsonProperty("name") String name,
//                 @JsonProperty("surname") String surname,
//                 @JsonProperty("registrationNumber") String registrationNumber,
//                 @JsonProperty("parentId") String parentId){
//        this.name = name;
//        this.surname = surname;
//        this.registrationNumber = registrationNumber;
//        this.parentId = parentId;
//    }
}
