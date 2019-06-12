package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "stops")
@NoArgsConstructor
@Getter @Setter
public class Stop {
    @Id
    private String id;

    private String stopName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm")
    private Date time;

    @JsonCreator
    public Stop(@JsonProperty("stopName") String stopName,@JsonProperty("time") Date time) {
        this.time = time;
        this.stopName = stopName;
    }
}
