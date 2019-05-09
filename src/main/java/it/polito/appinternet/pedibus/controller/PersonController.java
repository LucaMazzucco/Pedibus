package it.polito.appinternet.pedibus.controller;
import it.polito.appinternet.pedibus.model.Person;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.PersonRepository;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class PersonController {

    @Autowired
    PersonRepository personRepo;
    @PostConstruct
    public void init() {
    }

    @GetMapping("/insertStop")
    public String insertStop(Person person){
        personRepo.insert(person);
        return "Stop inserted correctly";
    }

}

