package it.polito.appinternet.pedibus.service;

import it.polito.appinternet.pedibus.model.Ride;
import it.polito.appinternet.pedibus.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    @Autowired
    RideRepository rideRepository;

    public List<Ride> getRidesNotConfirmed(){
        return rideRepository.findByConfirmedIsFalseOrConfirmedBackIsFalse();
    }
}
