package it.polito.appinternet.pedibus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.polito.appinternet.pedibus.controller.StopController;
import it.polito.appinternet.pedibus.model.Line;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import it.polito.appinternet.pedibus.controller.LineController;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class PedibusApplication {


    @Autowired
    StopRepository stopRepo;

    @Autowired
    private LineRepository lines;
    public static void main(String[] args) {
        SpringApplication.run(PedibusApplication.class, args);
    }

    Logger logger = LoggerFactory.getLogger(PedibusApplication.class);
    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Stop[] newStops = mapper.readValue(new FileReader("src/main/data/stops.json"), Stop[].class);
            for(Stop a : newStops){
                logger.info(a.toString());
                stopRepo.save(a);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}