package it.polito.appinternet.pedibus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shifts")
public class Shift {
    @Id
    private String id;

    private String email;
    private String lineName;
    private long rideDate;
    private boolean flagGoing;
    private boolean confirmed1;
    private boolean confirmed2;

    public Shift(String email, String lineName, long rideDate, boolean flagGoing, boolean confirmed1, boolean confirmed2){
        this.email = email;
        this.lineName = lineName;
        this.rideDate = rideDate;
        this.flagGoing = flagGoing;
        this.confirmed1 = confirmed1;
        this.confirmed2 = confirmed2;
    }
}
