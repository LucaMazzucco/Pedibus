package it.polito.appinternet.pedibus.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ConfirmationToken {

    @Id

    private String confirmationToken;

    public ConfirmationToken(){
        confirmationToken = UUID.randomUUID().toString();
    }
}
