package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.*;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200/presenze", maxAge = 3600)
@RestController
public class LineController {

    @Autowired
    LineRepository lineRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ReservationRepository reservationRepo;


    @GetMapping("/insertLine")
    public String insertLine(Line l) {
        //TO DO: Controlli sulle stop della line da inserire e persone se presenti
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

    @GetMapping("/getLines")
    public String getLines(){
        List<Line> allLines = lineRepo.findAll();
        JSONArray lines = new JSONArray();
        for(Line l:allLines){
            lines.put(encapsulateLine(l));
        }
        return lines.toString();
    }

    @GetMapping("/getLines/{line_name}")
    public String getLine(@PathVariable String line_name){
        Line line = lineRepo.findByLineName(line_name);
        if(line!=null){
            return encapsulateLine(line).toString();
        }
        return "";
    }

    private JSONObject encapsulateLine(Line line){
        JSONObject lineJson = new JSONObject();
        JSONArray ridesJson = new JSONArray();
        for(Ride r:line.getRides()){
            JSONObject rideJson = new JSONObject();
            JSONArray stopsJson = new JSONArray();
            JSONArray stopsBackJson = new JSONArray();
            JSONArray notReserved = new JSONArray();
            JSONArray notReservedBack = new JSONArray();
            Ride r2 = line.getRides().stream()
                    .filter(x->x.getRideDate().getDate()==r.getRideDate().getDate())
                    .filter(x->x.getFlagAndata()!=r.getFlagAndata())
                    .findFirst().orElse(null);
            if(!r.getFlagAndata()){
                if(r2!=null){
                    continue;
                }
                else{
                   stopsBackJson = encapsulateStops(r,line.getStopListR());
                    notReservedBack = encapsulateNotReserved(r2);
                }
            }
            else{
                stopsJson = encapsulateStops(r,line.getStopListA());
                notReserved = encapsulateNotReserved(r);
                if(r2!=null){
                    stopsBackJson = encapsulateStops(r2,line.getStopListA());
                    notReservedBack = encapsulateNotReserved(r2);
                }
            }
            rideJson.put("date",r.getRideDate());
            rideJson.put("stops",stopsJson);
            rideJson.put("stopsBack",stopsBackJson);
            rideJson.put("notReserved",notReserved);
            rideJson.put("notReservedBack",notReservedBack);
            ridesJson.put(rideJson);
        }
        lineJson.put("lineName",line.getLineName());
        lineJson.put("rides",ridesJson);
        return lineJson;
    }

    private JSONArray encapsulateStops(Ride ride,List<Stop> stops){
        JSONArray stopsJson = new JSONArray();
        HashMap<String,JSONObject> stopsJsonMap = new HashMap<>();
        stops.forEach(s->{
            JSONObject stopJson = new JSONObject();
            stopJson.put("stopName",s.getStopName());
            stopJson.put("time",s.getTime());
            stopsJsonMap.put(s.getStopName(),stopJson);
        });
        ride.getReservations().stream()
                .map(e->reservationRepo.findById(e))
                .collect(Collectors.toMap(e->e.getStopName(),e->e))
                .entrySet().stream()
                .collect(Collectors.groupingBy(e->e.getKey(),HashMap::new,Collectors.toList()))
                .forEach((k,v)->{
                    JSONArray people = new JSONArray();
                    v.forEach(e->people.put(
                            encapsulateUser(
                                    e.getValue().getPassenger(),e.getValue().isPresent()
                            )
                            )
                    );
                    if(stopsJsonMap.containsKey(k)){
                        stopsJsonMap.get(k).put("people",people);
                    }
                })
        ;
        stopsJsonMap.values().forEach(s->stopsJson.put(s));
        return stopsJson;
    }

    private JSONObject encapsulateUser(User user,Boolean isPresent){
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("name", user.getName());
        jsonUser.put("surname",user.getSurname());
        jsonUser.put("isPresent",isPresent);
        return jsonUser;
    }

    private JSONArray encapsulateNotReserved(Ride ride){
        JSONArray notReserved = new JSONArray();
        userRepo.findAll().stream()
                .filter(u->
                        ride.getReservations().stream()
                                .map(x -> reservationRepo.findById(x).getPassenger().getRegistrationNumber())
                                .noneMatch(x -> x.equals(u.getRegistrationNumber()))
                )
                .forEach(u->
                        notReserved.put(encapsulateUser(u,false))
                );
        return notReserved;
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
