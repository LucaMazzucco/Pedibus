package it.polito.appinternet.pedibus.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.service.LineService;
import it.polito.appinternet.pedibus.service.ShiftService;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "http://localhost:4200/presenze", maxAge = 3600)
@RestController
public class LineController {

    @Autowired
    LineService lineService;
    @Autowired
    ShiftService shiftService;

    @GetMapping("/line/insertLine")
    public ResponseEntity<String> insertLine(Line l) {
        if(l==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(lineService.insertLine(l),HttpStatus.OK);
    }

    @GetMapping("/line/lines")
    public String findAllLines(){
        List<String> linesNames = lineService.findAllLinesNames();
        JSONArray json = new JSONArray();
        for(String lineName : linesNames){
            json.put(lineName);
        }
        return json.toString();
    }

    //Get json of all lines
    @GetMapping("/line/getLines")
    public String getLinesJson(){
        List<Line> allLines = lineService.findAll();
        JSONArray lines = new JSONArray();
        for(Line l:allLines){
            lines.put(lineService.encapsulateLine(l));
        }
        return lines.toString();
    }

    //Get json of a single line
    @GetMapping("/line/getLines/{line_name}")
    public String getLineJson(@PathVariable String line_name){
        Line line = lineService.findByLineName(line_name);
        if(line!=null){
            return lineService.encapsulateLine(line).toString();
        }
        return "";
    }

    //Get json of a single Ride A/R (frontend format)
    @GetMapping("/line/getLines/{line_name}/{date}/{flagGoing}")
    public String getRideJson(@PathVariable String line_name,
                          @PathVariable long date,
                              @PathVariable boolean flagGoing){
        JSONObject returnJson = new JSONObject();
        try{
            Line line = lineService.findByLineName(line_name);
            Ride ride = lineService.getRideByLineAndDateAndFlagGoing(line,date,flagGoing);
            if(ride==null){
                return "";
            }
            returnJson = lineService.encapsulateRide(ride, line);
        } catch (NullPointerException e){
            e.printStackTrace();
            return "";
        }
        return returnJson.toString();
    }

    @PutMapping("/line/putLineAttendance/{line_name}")
    public ResponseEntity updateLineToUpdatePassengersInfo(@PathVariable String line_name, @RequestBody String payload) {
        if (line_name == null || payload == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        int result = lineService.updateLineToUpdatePassengersInfo(line_name, payload);
        if (result < 0) {
            new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/line/putLineAttendance/{line_name}/ride")
    public ResponseEntity updateRideToUpdatePassengersInfo(@PathVariable String line_name,
                                                                   @RequestBody String payload){
        if(line_name==null || payload == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(lineService.updateRideToUpdatePassengersInfo(line_name,payload)<0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/line/putLineAttendance/{line_name}/ride/user")
    public ResponseEntity<String> updateUserToUpdatePassengersInfo(@PathVariable String line_name,
                                                                   @RequestBody String payload) {
        if (line_name == null || payload == null) {
            return ResponseEntity.badRequest().build();
        }
        JSONObject mainJson = new JSONObject(payload);
        if(!mainJson.has("person") ||
            !mainJson.has("rideDate") ||
            !mainJson.has("isBack")
        ){
            return ResponseEntity.badRequest().build();
        }
        JSONObject userJson = mainJson.getJSONObject("person");
        if(!userJson.has("isPresent") ||
            !userJson.has("registrationNumber") ||
            !userJson.has("name") ||
            !userJson.has("surname")
        ){
            return ResponseEntity.badRequest().build();
        }
        // No stop check required bc for the same line,date,direction there can be only one reservation (on a ride)
        // for the same registrationNumber whatever the stop
        boolean isFlagGoing = !mainJson.getBoolean("isBack");
        boolean isPresent = userJson.getBoolean("isPresent");
        long rideDate = mainJson.getLong("rideDate");
        String registrationNumber = userJson.getString("registrationNumber");
        int result = lineService.updateUserToUpdatePassengersInfo(line_name,
                rideDate, registrationNumber, isPresent, isFlagGoing);
        if(result<0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/line/lines/{line_name}")
    public String findLineByName(@PathVariable String line_name){
        Line line = lineService.findByLineName(line_name);
//        JSONArray stopA = new JSONArray();
//        JSONArray stopR = new JSONArray();
//        for(Stop s : found.getStopListA()){
//            stopA.put(s.getStopName());
//        }
//        for(Stop s : found.getStopListR()){
//            stopR.put(s.getStopName());
//        }
//        JSONObject mainObj = new JSONObject();
//        mainObj.put("stopListA", stopA);
//        mainObj.put("stopListR", stopR);
        return lineService.encapsulateLine(line).toString();
    }


    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Line[] newLines = mapper.readValue(new FileReader("./src/main/data/lines.json"), Line[].class);
            for(Line l : newLines){
                lineService.insertLine(l);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/line/getNoAdminLines")
    public String getNoAdminLines(){
        List<Line> allLines = lineService.findNoAdminLines();
        JSONArray lines = new JSONArray();
        for(Line l:allLines){
            lines.put(lineService.encapsulateLine(l));
        }
        return lines.toString();
    }

    @GetMapping("/line/getStopNames/{lineName}")
    public ResponseEntity<String> getStopNamesByLineName(@PathVariable String lineName){
        if(lineName.length()==0) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        JSONArray stopNames = lineService.getStopNamesByLineName(lineName);
        if(stopNames== null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stopNames.toString(),HttpStatus.OK);
    }
}
