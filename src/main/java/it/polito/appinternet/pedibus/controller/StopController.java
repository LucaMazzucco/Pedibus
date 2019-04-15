package it.polito.appinternet.pedibus.controller;

import com.google.gson.Gson;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class StopController {

    @Autowired
    StopRepository stopRepo;
    @PostConstruct
    public void init() {
        try {
            FileReader reader = new FileReader("src/main/data/lines.json");
            Gson gsonStops = new Gson();
            Stop[] newStops = gsonStops.fromJson(reader, Stop[].class);
            for(Stop a : newStops){
                insertStop(a);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/insertStop")
    public String insertStop(Stop s){
        stopRepo.save(s);
        return "Stop inserted correctly";
    }

    /*
    public List<Long> getStopID(){
        List<Stop> stops = stopRepo.findAll();
        for (Stop s : stops){
            s.getId();
        }
    }
    */


}
