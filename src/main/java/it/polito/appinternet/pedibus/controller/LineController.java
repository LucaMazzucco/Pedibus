package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.service.LineService;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200/presenze", maxAge = 3600)
@RestController
public class LineController {

    @Autowired
    LineService lineService;

    @GetMapping("/insertLine")
    public ResponseEntity<String> insertLine(Line l) {
        if(l==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(lineService.insertLine(l),HttpStatus.OK);
    }

    @GetMapping("/lines")
    public String findAllLines(){
        List<String> linesNames = lineService.findAllLinesNames();
        JSONArray json = new JSONArray();
        for(String lineName : linesNames){
            json.put(lineName);
        }
        return json.toString();
    }

    //Get json of all lines
    @GetMapping("/getLines")
    public String getLinesJson(){
        List<Line> allLines = lineService.findAll();
        JSONArray lines = new JSONArray();
        for(Line l:allLines){
            lines.put(lineService.encapsulateLine(l));
        }
        return lines.toString();
    }

    //Get json of a single line
    @GetMapping("/getLines/{line_name}")
    public String getLineJson(@PathVariable String line_name){
        Line line = lineService.findByLineName(line_name);
        if(line!=null){
            return lineService.encapsulateLine(line).toString();
        }
        return "";
    }

    //Get json of a single Ride A/R (frontend format)
    @GetMapping("/getLines/{line_name}/{date}")
    public String getRideJson(@PathVariable String line_name,
                          @PathVariable long date){
        JSONObject returnJson = new JSONObject();
        try{
            Line line = lineService.findByLineName(line_name);
            Ride ride = lineService.getRideByLineAndDate(line,date);
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

    @PutMapping("/putLineAttendance/{line_name}")
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

    @PutMapping("putLineAttendance/{line_name}/ride")
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

    @PutMapping("putLineAttendance/{line_name}/ride/user")
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

    @GetMapping("/lines/{line_name}")
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

    @GetMapping("/getAvailabilities/{email}")
    public String getAvailabilities(@PathVariable String email){
        if(email == null){
            return "-1";
        }
        List<Line> myLines = lineService.getLineOfCompanions(email);
        JSONArray availabilities = new JSONArray();
        JSONObject tmp = new JSONObject();
        myLines.forEach(l -> {
            List<Ride> myRides = l.getRides().stream().filter(r -> r.getCompanions().contains(email)).collect(Collectors.toList());
            myRides.forEach(r -> {
                tmp.put("email", email);
                tmp.put("lineName", l.getLineName());
                tmp.put("rideDate", r.getRideDate());
                tmp.put("confirmed", r.isConfirmed());
                tmp.put("flagGoing", r.isFlagGoing());
                availabilities.put(tmp);
            });
        });

        return availabilities.toString();
    }

    @PostMapping("/deleteAvailability")
    public ResponseEntity deleteAvailability(@RequestBody String payload) {
        JSONObject mainJson = new JSONObject(payload);
        Shift avToDelete = lineService.generateShift(mainJson);
        if (avToDelete == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            if (lineService.deleteAvailability(avToDelete) < 0) {
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            } else {
                Message m = new Message(Instant.now().getEpochSecond(), false, "L'accompagnatore: " + avToDelete.getEmail() + " ha tolto la disponibilità per la linea: " + avToDelete.getLineName());
                if (lineService.sendMessageLineAdmin(avToDelete.getLineName(), m) < 0) {
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity(HttpStatus.OK);
                }
            }
        }
    }

    @GetMapping("/getShifts/{email}")
    public String getShifts(@PathVariable String email){
        if(email == null){
            return "-1";
        }
        List<Line> myLines = lineService.getLineShifts(email);
        JSONArray availabilities = new JSONArray();
        JSONObject tmp = new JSONObject();
        myLines.forEach(l -> {
            List<Ride> myRides = l.getRides().stream().filter(r -> !r.isConfirmed()).collect(Collectors.toList());
            myRides.forEach(r -> {
                r.getCompanions().forEach(c -> {
                    tmp.put("email", c);
                    tmp.put("lineName", l.getLineName());
                    tmp.put("rideDate", r.getRideDate());
                    tmp.put("confirmed", r.isConfirmed());
                    tmp.put("flagGoing", r.isFlagGoing());
                    availabilities.put(tmp);
                });
            });
        });

        return availabilities.toString();
    }

    @PostMapping("/addShift")
    public ResponseEntity addShift(@RequestBody String payload){
        JSONObject mainJson = new JSONObject(payload);
        Shift newShift = lineService.generateShift(mainJson) ;
        if(lineService.addNewShift(newShift, true) < 0){
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            }
        else{
        Message m = new Message(Instant.now().getEpochSecond(), false, "L'amministratore ti ha confermato come accompagnatore per la linea " + newShift.getLineName());
        if(lineService.sendMessageToUser(newShift.getEmail(), m) < 0){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            else{
                return new ResponseEntity(HttpStatus.OK);
            }
        }
    }

    @PostMapping("/deleteShift")
    public ResponseEntity removeShift(@RequestBody String payload){
        JSONObject mainJson = new JSONObject(payload);
        Shift newShift = lineService.generateShift(mainJson) ;
        if(lineService.addNewShift(newShift, false) < 0){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else{
            Message m = new Message(Instant.now().getEpochSecond(), false, "L'amministratore ti ha cancellato il turno per la linea: " + newShift.getLineName());
            if((lineService.sendMessageToUser(newShift.getEmail(), m)) < 0){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            else{
                return new ResponseEntity(HttpStatus.OK);
            }
        }
    }


    @PostMapping("/addAvailability")
    public ResponseEntity addAvailability(@RequestBody String payload) {
        JSONObject mainJson = new JSONObject(payload);
        Shift newAvailability = lineService.generateShift(mainJson);
        if (lineService.addNewAvailability(newAvailability) < 0) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        } else {
            Message m = new Message(Instant.now().getEpochSecond(), false, "L'accompagnatore " + newAvailability.getEmail() + " è disponibile per la linea " + newAvailability.getLineName());
            if (lineService.sendMessageLineAdmin(newAvailability.getLineName(), m) < 0) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity(HttpStatus.OK);
            }
        }
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

    @GetMapping("/getNoAdminLines")
    public String getNoAdminLines(){
        List<Line> allLines = lineService.findNoAdminLines();
        JSONArray lines = new JSONArray();
        for(Line l:allLines){
            lines.put(lineService.encapsulateLine(l));
        }
        return lines.toString();
    }

    @GetMapping("/getStopNames/{lineName}")
    public ResponseEntity<JSONObject> getStopNamesByLineName(@PathVariable String lineName){
        if(lineName.length()==0) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        JSONObject stopNames = lineService.getStopNamesByLineName(lineName);
        if(stopNames== null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stopNames,HttpStatus.OK);
    }
}
