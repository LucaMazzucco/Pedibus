package it.polito.appinternet.pedibus;

import com.google.gson.Gson;
import it.polito.appinternet.pedibus.model.Line;
import it.polito.appinternet.pedibus.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import it.polito.appinternet.pedibus.controller.LineController;
import java.io.FileNotFoundException;
import java.io.FileReader;

@SpringBootApplication
public class PedibusApplication {


    @Autowired
    private LineRepository lines;
    public static void main(String[] args) {
        SpringApplication.run(PedibusApplication.class, args);
    }

}