package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Message {

    private long timestamp;
    private boolean read;
    private String body;

    public Message(long timestamp, boolean read, String body) {
        this.timestamp = timestamp;
        this.read = read;
        this.body = body;
    }
}
