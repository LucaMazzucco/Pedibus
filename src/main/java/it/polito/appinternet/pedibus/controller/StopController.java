package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class StopController {

    @Autowired
    StopRepository stopRepo;


    @GetMapping("/insertStop")
    public String insertStop(Stop s){
        stopRepo.insert(s);
        return "Stop inserted correctly";
    }


    public List<String> getStopID() {
        List<Stop> stops = stopRepo.findAll();
        return stops.stream().map(s->s.getId()).collect(Collectors.toList());
    }

    @PostConstruct
    public void init(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            Stop[] newStops = mapper.readValue(new FileReader("src/main/data/stops.json"), Stop[].class);
            for(Stop a : newStops){
                stopRepo.insert(a);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
