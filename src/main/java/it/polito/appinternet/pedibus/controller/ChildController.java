package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.service.ChildService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChildController {

    @Autowired
    ChildService childService;

    @GetMapping("/children/getChildReservations/{ssn}")
    public ResponseEntity<String> getChildrenReservation(@PathVariable String ssn){
        if(ssn.length()==0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        JSONArray jsonArray = childService.encapsulateReservations(ssn);
        if(jsonArray==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(jsonArray.toString(),HttpStatus.OK);
    }

    @PostMapping("/children/addChildReservations/{ssn}")
    public ResponseEntity addChildReservations(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.addChildReservations(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/children/addChildReservation/{ssn}")
    public ResponseEntity addChildReservation(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.addChildReservation(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/children/editChildReservation/{ssn}")
    public ResponseEntity editChildReservation(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.editChildReservation(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/children/deleteChildReservation/{ssn}")
    public ResponseEntity deleteChildReservation(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.deleteChildReservation(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
