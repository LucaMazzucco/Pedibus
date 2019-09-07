package it.polito.appinternet.pedibus.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polito.appinternet.pedibus.model.Child;
import it.polito.appinternet.pedibus.model.Reservation;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.ChildRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildService {
    @Autowired
    ChildRepository childRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;


    public Child findByRegistrationNumber(String registrationNumber) {
        return childRepo.findByRegistrationNumber(registrationNumber);
    }

    public Child findById(String id){
        return childRepo.findById(id);
    }

    public List<Child> findAll(){
        return childRepo.findAll();
    }

    public JSONObject encapsulateChildOnRide(Child child, Boolean isPresent){
        JSONObject jsonChild = new JSONObject();
        jsonChild.put("name", child.getName());
        jsonChild.put("surname", child.getSurname());
        jsonChild.put("registrationNumber", child.getRegistrationNumber());
        jsonChild.put("isPresent",isPresent);
        return jsonChild;
    }

    public JSONObject encapsulateChildInfo(Child child){
        JSONObject jsonChild = new JSONObject();
        jsonChild.put("name",child.getName());
        jsonChild.put("surname", child.getSurname());
        jsonChild.put("registrationNumber", child.getRegistrationNumber());
        return jsonChild;
    }

    public JSONObject encapsulateReservations(String ssn){
        Child child = findByRegistrationNumber(ssn);
        if(child == null){
            return null;
        }
        User parent = userService.userFindById(child.getParentId());
        List<Reservation> resList = parent.getReservations().stream()
                .map(res->reservationService.findById(res))
                .filter(res->res.getChild().equals(child.getId()))
                .collect(Collectors.toList());
        return reservationService.encapsulateChildReservations(resList,child,parent);
    }
}
