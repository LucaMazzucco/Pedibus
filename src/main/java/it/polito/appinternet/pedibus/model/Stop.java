package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stops")
@NoArgsConstructor
public class Stop {

    @Getter @Setter
    private String stopName;

    @JsonCreator
    public Stop(String stopName) {
        this.stopName = stopName;
    }
}
