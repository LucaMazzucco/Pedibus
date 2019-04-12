package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import it.polito.appinternet.pedibus.model.Line;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class LineController {

    @Autowired
    LineRepository lineRepo;

    @GetMapping("/insertLine")
    public String insertLine(){
        Set<String> stops = new HashSet<String>();
        stops.add("Stop1");
        stops.add("Stop2");
        lineRepo.save(new Line("Linea1"));
        return "Line inserted correctly";
    }

    @GetMapping("/findAllLines")
    public List<Line> findAllLines(){
        List<Line> allLines = lineRepo.findAll();
        return allLines;
    }
}
