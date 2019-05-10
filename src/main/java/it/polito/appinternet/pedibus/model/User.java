package it.polito.appinternet.pedibus.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private Long userId;

    private String email;

    private String password;

    private  boolean isEnabled = false;
}
