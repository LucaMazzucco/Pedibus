package it.polito.appinternet.pedibus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polito.appinternet.pedibus.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import it.polito.appinternet.pedibus.model.Line;
import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    LineRepository lineRepo;

    @PostConstruct
    public void init() {
        try {
            FileReader reader = new FileReader("src/main/data/lines.json");
            Gson gsonLines = new Gson();
            Line[] newLines = gsonLines.fromJson(reader, Line[].class);
            for(Line a : newLines){
                insertLine(a);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/insertLine")
    public String insertLine(Line l){
        lineRepo.save(l);
        return "Line inserted correctly";
    }

    @GetMapping("/lines")
    public String findAllLines(){
        List<Line> allLines = lineRepo.findAll();
        JsonArray lineNames = new JsonArray();
        for(Line line : allLines){
            lineNames.add(line.getLineName());
        }
        return lineNames.toString();
    }


}
