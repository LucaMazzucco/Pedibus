package it.polito.appinternet.pedibus.controller;

import com.mongodb.util.JSON;
import it.polito.appinternet.pedibus.service.ChildService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

}
