package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.Ride;
import it.polito.appinternet.pedibus.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.FileReader;

@RestController
public class RideController {

    @Autowired
    RideRepository rideRepo;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Ride[] newRides = mapper.readValue(new FileReader("src/main/data/rides.json"), Ride[].class);
            for(Ride ride : newRides){
                rideRepo.insert(ride);
            }
            return;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
