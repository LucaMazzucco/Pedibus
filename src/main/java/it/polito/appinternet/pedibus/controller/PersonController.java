package it.polito.appinternet.pedibus.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.Person;
import it.polito.appinternet.pedibus.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.FileReader;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepo;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Person[] newRes = mapper.readValue(new FileReader("src/main/data/persons.json"), Person[].class);
            for(Person p : newRes){
                personRepo.insert(p);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/intertPerson")
    public String insertStop(Person person){
        personRepo.insert(person);
        return "Stop inserted correctly";
    }

}

