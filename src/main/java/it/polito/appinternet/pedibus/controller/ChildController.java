package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.service.ChildService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChildController {
    @Autowired
    ChildService childService;

    @GetMapping("/getChildReservations/{ssn}")
    public ResponseEntity<JSONObject> getChildren(@PathVariable String ssn){
        if(ssn.length()==0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        JSONObject jsonObject = childService.encapsulateReservations(ssn);
        if(jsonObject==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jsonObject,HttpStatus.OK);
    }

    @PostMapping("/addChildReservations/{ssn}")
    public ResponseEntity addChildReservations(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.addChildReservations(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/addChildReservation/{ssn}")
    public ResponseEntity addChildReservation(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.addChildReservation(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/editChildReservation/{ssn}")
    public ResponseEntity editChildReservation(@PathVariable String ssn, @RequestBody String payload){
        if(ssn.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(childService.editChildReservation(ssn,payload)){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/deleteChildReservation/{ssn}")
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
