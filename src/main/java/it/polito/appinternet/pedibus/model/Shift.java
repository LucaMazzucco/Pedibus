package it.polito.appinternet.pedibus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    private String email;
    private String lineName;
    private long rideDate;
    private Boolean flagGoing;
    private Boolean confirmed;

}
