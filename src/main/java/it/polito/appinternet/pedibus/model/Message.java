package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class Message {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private Date timestamp;
    private boolean read;
    private String body;

    public Message(Date timestamp, boolean read, String body) {
        this.timestamp = timestamp;
        this.read = read;
        this.body = body;
    }
}
