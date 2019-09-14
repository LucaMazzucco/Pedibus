package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.service.ReservationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    @PostConstruct
    public void init() {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectMapper rideMapper = new ObjectMapper();
//        try {
//            Ride[] newRides = rideMapper.readValue(new FileReader("src/main/data/rides.json"), Ride[].class);
//            Reservation[] newRes = mapper.readValue(new FileReader("src/main/data/reservation.json"), Reservation[].class);
//            List<Reservation> reservationsSupportList = new LinkedList<>();
//            for(Ride ride : newRides){
//                for(Reservation r : newRes){
//                    if(ride.getLineName().equals(r.getLineName()) &&
//                            ride.getRideDate().getDate() == r.getReservationDate().getDate() &&
//                            ride.getFlagGoing() == r.isFlagGoing()
//                    ){
//                        r = reservationRepo.insert(r);
//                        ride.getReservations().add(r.getId());
//                    }
//                    else{
//                        reservationsSupportList.add(r);
//                    }
//                }
//                rideRepo.insert(ride);
//                newRes = reservationsSupportList.toArray(new Reservation[reservationsSupportList.size()]);
//                reservationsSupportList = new LinkedList<>();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

//    @GetMapping("/insertReservation")
//    public String insertLine(Reservation reservation){
//        reservationRepo.insert(reservation);
//        return "Reservation inserted correctly";
//    }
    @GetMapping("reservations/{line_name}/{date}/{reservation_id}")
    public ResponseEntity<String> findByDateAndLineAndId(@PathVariable String line_name, @PathVariable long date, @PathVariable String reservation_id){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Reservation reservation = reservationService.findByDateAndLineAndId(line_name, date, reservation_id);
            if(reservation!=null){
                return ResponseEntity.ok(objectMapper.writeValueAsString(reservation));
            }
            else{
                return ResponseEntity.notFound().build();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    //Not needed anymore i guess
//    @SuppressWarnings("Duplicates")
//    @GetMapping("/reservations/{line_name}/{date}")
//    public String findByDateAndLine(@PathVariable String line_name, @PathVariable String date){

    @SuppressWarnings("Duplicates")
    @PostMapping("/reservations/{line_name}/{date}")
    public ResponseEntity addReservation(@PathVariable String line_name, @PathVariable long date, @RequestBody String payload){
        JSONObject main_json = new JSONObject(payload);
        if(!main_json.has("flagGoing") ||
            !main_json.has("stopName") ||
            !main_json.has("passenger") ||
                !main_json.has("isPresent")
            ){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        JSONObject p_json = main_json.getJSONObject("passenger");
        if(!p_json.has("registrationNumber") ||
            !p_json.has("name") ||
            !p_json.has("surname")
            ){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        String registrationNumber = p_json.getString("registrationNumber");
        String stopName = main_json.getString("stopName");
        Boolean flagGoing = main_json.getBoolean("flagGoing");
        Boolean isPresent = main_json.getBoolean("isPresent");
        if(flagGoing==null || isPresent == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Reservation reservation = reservationService.addReservation(line_name, date,
                registrationNumber, stopName, flagGoing, isPresent);
        if(reservation != null){
            return new ResponseEntity<>(reservation.getId(), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("Duplicates")
    @PutMapping("/reservations/{line_name}/{date}/{reservation_id}")
    @Transactional
    public ResponseEntity updateReservation(@PathVariable String line_name, @PathVariable long date, @PathVariable String reservation_id, @RequestBody String payload){
        Reservation newRes = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            newRes = mapper.readValue(payload, Reservation.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(newRes==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(reservationService.updateReservation(line_name,date,reservation_id,newRes)>=0){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("Duplicates")
    @DeleteMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public ResponseEntity deleteReservation(@PathVariable String line_name, @PathVariable long date, @PathVariable String reservation_id){
        if(reservationService.deleteReservation(line_name,date,reservation_id)<0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}

