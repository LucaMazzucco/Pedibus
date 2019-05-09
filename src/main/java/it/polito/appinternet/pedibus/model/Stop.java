package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stops")
@NoArgsConstructor
public class Stop {

    @Id
    @Getter @Setter
    private String id;

    @Getter @Setter
    private String stopName;

    @JsonCreator
    public Stop(@JsonProperty("stopName") String stopName) {
        this.stopName = stopName;
    }
}
