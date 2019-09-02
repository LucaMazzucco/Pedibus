package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class ConfirmationToken {
    @Id
    private String id;
    private String confirmationToken;
    private Date createdDate;
    private String user_email;
    private String role;

    public ConfirmationToken(String email, String role) {
        this.user_email = email;
        this.role = role;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        createdDate = cal.getTime();
        confirmationToken = UUID.randomUUID().toString();
    }
}
