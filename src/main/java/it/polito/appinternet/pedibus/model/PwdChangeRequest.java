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
@Getter
@Setter
public class PwdChangeRequest {
    @Id
    private String id;
    private String token;
    private Date expirationDate;
    private User user;
    private int tokenDuration = 1;

    public PwdChangeRequest(User u){
        this.user = u;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, tokenDuration);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        expirationDate = cal.getTime();
        token = UUID.randomUUID().toString();
    }
}
