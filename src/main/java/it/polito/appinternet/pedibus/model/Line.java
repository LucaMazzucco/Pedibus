package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "lines")
@NoArgsConstructor
public class Line{

    @Getter @Setter
    private String lineName;

    @Getter @Setter
    private List<String> stopListA;

    @Getter @Setter
    private List<String> stopListR;

    public Line(String lineName, List<String> stopListA, List<String> stopListR) {
        this.lineName = lineName;
        this.stopListA = stopListA;
        this.stopListR = stopListR;
    }

    @Override
    public String toString() {
        return lineName + stopListA + stopListR;
    }
}
