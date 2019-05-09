package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class StopController {

    /*
    @GetMapping("/insertStop")
    public String insertStop(Stop s){
        stopRepo.insert(s);
        return "Stop inserted correctly";
    }*/

    /*
    public List<Long> getStopID(){
        List<Stop> stops = stopRepo.findAll();
        for (Stop s : stops){
            s.getId();
        }
    }
    */


}
