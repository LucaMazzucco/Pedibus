package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import it.polito.appinternet.pedibus.model.Line;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    StopRepository stopRepo;

    @Autowired
    LineRepository lineRepo;

    @GetMapping("/insertLine")
    public String insertLine(Line l) {
        //TODO: Controlli sulle stop della line da inserire e persone se presenti
        lineRepo.insert(l);
        return "Line inserted correctly";
    }


    @GetMapping("/lines")
    public String findAllLines(){
        List<Line> allLines = lineRepo.findAll();
        JSONArray lineNames = new JSONArray();
        for(Line line : allLines){
            lineNames.put(line.getLineName());
        }
        return lineNames.toString();
    }

    @GetMapping("/lines/{line_name}")
    public String findLineByName(@PathVariable String line_name){
        Line found = lineRepo.findByLineName(line_name);
        JSONArray stopA = new JSONArray();
        JSONArray stopR = new JSONArray();
        for(Stop s : found.getStopListA()){
            stopA.put(s.getStopName());
        }
        for(Stop s : found.getStopListR()){
            stopR.put(s.getStopName());
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("stopListA", stopA);
        mainObj.put("stopListR", stopR);
        return mainObj.toString();
    }

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Line[] newLines = mapper.readValue(new FileReader("src/main/data/lines.json"), Line[].class);
            for(Line l : newLines){
                lineRepo.insert(l);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
