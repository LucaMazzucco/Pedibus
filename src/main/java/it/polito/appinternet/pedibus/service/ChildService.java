package it.polito.appinternet.pedibus.service;

import it.polito.appinternet.pedibus.model.Child;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.ChildRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChildService {
    @Autowired
    ChildRepository childRepo;


    public Child findByRegistrationNumber(String registrationNumber) {
        return childRepo.findByRegistrationNumber(registrationNumber);
    }
    public Child findById(String id){
        return childRepo.findById(id);
    }
    public List<Child> findAll(){
        return childRepo.findAll();
    }

    public JSONObject encapsulateChild(Child child, Boolean isPresent){
        JSONObject jsonChild = new JSONObject();
        jsonChild.put("name", child.getName());
        jsonChild.put("surname", child.getSurname());
        jsonChild.put("registrationNumber", child.getRegistrationNumber());
        jsonChild.put("isPresent",isPresent);
        return jsonChild;
    }
}
