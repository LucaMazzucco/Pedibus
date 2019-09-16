package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.Line;
import it.polito.appinternet.pedibus.model.Message;
import it.polito.appinternet.pedibus.model.Ride;
import it.polito.appinternet.pedibus.model.Shift;
import it.polito.appinternet.pedibus.service.LineService;
import it.polito.appinternet.pedibus.service.ShiftService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ShiftController {

    @Autowired
    ShiftService shiftService;
    @Autowired
    LineService lineService;

    @GetMapping("shift/getAvailabilities/{email}")
    public String getAvailabilities(@PathVariable String email){
        if(email == null){
            return "-1";
        }
        JSONArray availabilities = new JSONArray();
        shiftService.findByEmail(email)
                .stream()
                // .filter(shift-> !shift.isConfirmed2())
                .map(shift-> shiftService.encapsulateShift(shift))
                .forEach(availabilities::put);
        /*
        <Line> myLines = lineService.getLineOfCompanions(email);
        JSONObject tmp = new JSONObject();
        myLines.forEach(l -> {
            List<Ride> myRides = l.getRides().stream().filter(r -> r.getCompanions().contains(email)).collect(Collectors.toList());
            myRides.forEach(r -> {
                tmp.put("email", email);
                tmp.put("lineName", l.getLineName());
                tmp.put("rideDate", r.getRideDate());
                tmp.put("confirmed1",r.isConfirmed());
                tmp.put("confirmed2", r.isConfirmed());
                tmp.put("flagGoing", r.isFlagGoing());
                availabilities.put(tmp);
            });
        });
        */
        return availabilities.toString();
    }

    @PostMapping("shift/deleteAvailability")
    public ResponseEntity deleteAvailability(@RequestBody String payload) {
        JSONObject mainJson = new JSONObject(payload);
        Shift avToDelete = shiftService.decapsulateShift(mainJson);
        if (avToDelete == null || avToDelete.getId() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            if (lineService.deleteAvailability(avToDelete) < 0) {
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            } else {
                shiftService.deleteShift(avToDelete);
                Message m = new Message(Instant.now().getEpochSecond(), false, "L'accompagnatore: " + avToDelete.getEmail() + " ha tolto la disponibilità per la linea: " + avToDelete.getLineName());
                if (lineService.sendMessageLineAdmin(avToDelete.getLineName(), m) < 0) {
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity(HttpStatus.OK);
                }
            }
        }
    }

    @GetMapping("shift/getShifts/{email}")
    public String getShifts(@PathVariable String email){
        if(email == null){
            return "-1";
        }
        JSONArray availabilities = new JSONArray();
        shiftService.findByEmail(email)
                .stream()
                .filter(Shift::isConfirmed2)
                .map(shift-> shiftService.encapsulateShift(shift))
                .forEach(availabilities::put);
//        List<Line> myLines = lineService.getAdministratedLines(email);
//        JSONObject tmp = new JSONObject();
//        myLines.forEach(l -> {
//            List<Ride> myRides = l.getRides().stream().filter(r -> !r.isConfirmed()).collect(Collectors.toList());
//            myRides.forEach(r -> {
//                r.getCompanions().forEach(c -> {
//                    tmp.put("email", c);
//                    tmp.put("lineName", l.getLineName());
//                    tmp.put("rideDate", r.getRideDate());
//                    tmp.put("confirmed", r.isConfirmed());
//                    tmp.put("flagGoing", r.isFlagGoing());
//                    availabilities.put(tmp);
//                });
//            });
//        });

        return availabilities.toString();
    }

    @PostMapping("shift/confirm1")
    public ResponseEntity confirmShift1(@RequestBody String payload){
        JSONObject mainJson = new JSONObject(payload);
        Shift newShift = shiftService.decapsulateShift(mainJson) ;
        if(newShift == null || newShift.getId() == null){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else if(newShift.isConfirmed2()) return new ResponseEntity(HttpStatus.CONFLICT);
        else{
            newShift.setConfirmed1(true);
            shiftService.saveShift(newShift);
            Message m = new Message(Instant.now().getEpochSecond(), false, "L'amministratore ti ha confermato come accompagnatore per la linea " + newShift.getLineName());
            if(lineService.sendMessageToUser(newShift.getEmail(), m) < 0){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            else{
                return new ResponseEntity(HttpStatus.OK);
            }
        }
    }

    @PostMapping("shift/confirm2")
    public ResponseEntity confirmShift2(@RequestBody String payload){
        JSONObject mainJson = new JSONObject(payload);
        Shift newShift = shiftService.decapsulateShift(mainJson) ;
        if(newShift == null || !newShift.isConfirmed1() || !newShift.isConfirmed2()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        else if(lineService.addNewShift(newShift, true) < 0){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else{
            Message m = new Message(Instant.now().getEpochSecond(), false, "L'accompagnatore: " + newShift.getEmail() + " ha confermato l'assegnazione per la linea: " + newShift.getLineName());
            if (lineService.sendMessageLineAdmin(newShift.getLineName(), m) < 0) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity(HttpStatus.OK);
            }
        }
    }

    /*
    @PostMapping("shift/deleteShift")
    public ResponseEntity removeShift(@RequestBody String payload){
        JSONObject mainJson = new JSONObject(payload);
        Shift newShift = shiftService.decapsulateShift(mainJson) ;
        if(lineService.addNewShift(newShift, false) < 0){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else{
            Message m = new Message(Instant.now().getEpochSecond(), false, "L'amministratore ha cancellato il tuo turno per la linea: " + newShift.getLineName());
            if((lineService.sendMessageToUser(newShift.getEmail(), m)) < 0){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            else{
                return new ResponseEntity(HttpStatus.OK);
            }
        }
    }

     */

    @PostMapping("shift/addAvailability")
    public ResponseEntity addAvailability(@RequestBody String payload) {
        JSONObject mainJson = new JSONObject(payload);
        Shift newAvailability = shiftService.decapsulateShift(mainJson);
        if(newAvailability.isConfirmed1() || newAvailability.isConfirmed2())
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        newAvailability = shiftService.insertShift(newAvailability);
        if(newAvailability == null)
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
        else if (lineService.addNewAvailability(newAvailability) < 0) {
            shiftService.deleteShift(newAvailability);
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
}
