package it.polito.appinternet.pedibus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PwdChangePost {
    String pass1;
    String pass2;
    String token;
}
