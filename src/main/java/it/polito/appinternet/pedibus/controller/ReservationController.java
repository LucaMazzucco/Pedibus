package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.ReservationRepository;
import it.polito.appinternet.pedibus.repository.RideRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepo;

    @Autowired
    LineRepository lineRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RideRepository rideRepo;

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
//                            ride.getFlagAndata() == r.isFlagAndata()
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
    public ResponseEntity<String> findByDateAndLineAndId(@PathVariable String line_name, @PathVariable String date, @PathVariable String reservation_id){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date tmp_date = format.parse(date);
            Reservation reservation = reservationRepo.findByIdAndLineNameAndReservationDate(reservation_id,line_name,tmp_date);
            if(reservation!=null){
                return ResponseEntity.ok(objectMapper.writeValueAsString(reservation));
            }
            else{
                return ResponseEntity.notFound().build();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    @SuppressWarnings("Duplicates")
    @GetMapping("/reservations/{line_name}/{date}")
    public String findByDateAndLine(@PathVariable String line_name, @PathVariable String date){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date tmp_date = format.parse(date);
            List<Reservation> res_andata = reservationRepo.findByLineNameAndReservationDateAndFlagAndataIsTrue(line_name, tmp_date);
            List<Reservation> res_ritorno = reservationRepo.findByLineNameAndReservationDateAndFlagAndataIsFalse(line_name, tmp_date);
            Map<String, List<User>> personPerStopA= new HashMap<>();
            Map<String, List<User>> personPerStopR= new HashMap<>();
            for(Reservation r : res_andata){
                if(personPerStopA.containsKey(r.getStopName())){
                    personPerStopA.get(r.getStopName()).add(r.getPassenger());
                }
                else {
                    List<User> l = new LinkedList<>();
                    l.add(r.getPassenger());
                    personPerStopA.put(r.getStopName(), l);
                }
            }
            for(Reservation r : res_ritorno){
                if(personPerStopR.containsKey(r.getStopName())){
                    personPerStopR.get(r.getStopName()).add(r.getPassenger());
                }
                else {
                    List<User> l = new LinkedList<>();
                    l.add(r.getPassenger());
                    personPerStopR.put(r.getStopName(), l);
                }
            }
            JSONObject mainObj = new JSONObject();
            JSONObject jsonDataA = new JSONObject();
            JSONObject jsonDataR = new JSONObject();
            for(String key : personPerStopA.keySet()){
                jsonDataA.put(key, personPerStopA.get(key).toString());
            }
            mainObj.put("stopsA", jsonDataA);
            for(String key : personPerStopR.keySet()){
                jsonDataR.put(key, personPerStopR.get(key).toString());
            }
            mainObj.put("stopsR", jsonDataR);
            return mainObj.toString();

        } catch (Exception e){
            e.printStackTrace();
        }
        return "niente";
    }

    @SuppressWarnings("Duplicates")
    @PostMapping("/reservations/{line_name}/{date}")
    public Long addReservation(@PathVariable String line_name, @PathVariable String date, @RequestBody String payload){
        // To Test use addReservation.json
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date tmp_date;
        try{
            tmp_date = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Long(-1);
        }
        JSONObject main_json = new JSONObject(payload);
        if(!main_json.has("flagAndata") ||
            !main_json.has("stopName") ||
            !main_json.has("passenger") ||
                !main_json.has("isPresent")
            ){
            return new Long(-2);
        }
        Line l = lineRepo.findByLineName(line_name);
        JSONObject p_json = main_json.getJSONObject("passenger");
        if(!p_json.has("registrationNumber") ||
            !p_json.has("name") ||
            !p_json.has("surname")
            ){
            return new Long(-3);
        }
        User p = userRepo.findByRegistrationNumber(p_json.getString("registrationNumber"));
        String stopName = main_json.getString("stopName");
        Boolean flagAndata = main_json.getBoolean("flagAndata");
        if(flagAndata==null){
            return new Long(-4);
        }
        Stop s;
        if(flagAndata){
            s = l.getStopListA().stream()
                    .filter(x->x.getStopName().equals(stopName))
                    .findAny().orElse(null);
        }
        else{
            s = l.getStopListR().stream()
                    .filter(x->x.getStopName().equals(stopName))
                    .findAny().orElse(null);
        }
        Boolean isPresent = main_json.getBoolean("isPresent");
        Ride ride = l.getRides().stream()
                .filter(r->r.getRideDate().getDate()==tmp_date.getDate())
                .filter(r -> r.getFlagAndata()==flagAndata)
                .findAny().orElse(null);
        if(s==null || p==null || flagAndata==null || l==null || ride==null || isPresent==null){
            return new Long(-4);
        }
        if(ride.getReservations().stream()
                .map(x->reservationRepo.findById(x).getPassenger().getRegistrationNumber())
                .filter(x->x.equals(p.getRegistrationNumber()))
                .count() > 0
            ){
            return new Long(-5);
        }
        Reservation r = new Reservation(line_name,stopName,p,tmp_date,flagAndata,isPresent);
        r = reservationRepo.insert(r); //insert crea un nuovo id
        ride.getReservations().add(r.getId());
        lineRepo.save(l);
        return Long.getLong(r.getId());
    }

    @SuppressWarnings("Duplicates")
    @PutMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public Long updateReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable String reservation_id, @RequestBody String payload){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d;
        try{
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Long(-1);
        }
        Reservation r = reservationRepo.findById(reservation_id);
        Line lineName = lineRepo.findByLineName(line_name);
        if (r == null || lineName == null) {
            return new Long(-1);
        }
        Reservation newRes = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            newRes = mapper.readValue(payload, Reservation.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(newRes==null){
            return new Long(-2);
        }
        newRes.setId(r.getId());

        JSONObject mainJson = new JSONObject(payload);
        if(!mainJson.has("lineName") ||
                !mainJson.has("stopName") ||
                !mainJson.has("flagAndata") ||
                !mainJson.has("reservationDate") ||
                !mainJson.has("passenger") ||
                !mainJson.has("isPresent")
                ){
            return new Long(-2);
        }
        JSONObject pJson = mainJson.getJSONObject("passenger");
        if(!pJson.has("registrationNumber") ||
                !pJson.has("name") ||
                !pJson.has("surname")
        ){
            return new Long(-3);
        }
        Boolean flag = newRes.isFlagAndata();
        Ride ride = lineName.getRides().stream()
                .filter(x->x.getRideDate().getDate()==d.getDate())
                .filter(x->x.getFlagAndata()==flag)
                .findAny().orElse(null);
        if(ride==null){
            return new Long(-4);
        }
        reservationRepo.save(newRes); //Save sovrascrive sull'id selezionato
        //No need to update ride because there is no id or userEmail change
        return Long.valueOf(1);
    }

    @SuppressWarnings("Duplicates")
    @DeleteMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public void deleteReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable String reservation_id){
        Reservation reservation = reservationRepo.findById(reservation_id);
        if (reservation == null){
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d;
        try{
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        if(!reservation.getLineName().equals(line_name) || !reservation.getReservationDate().equals(d)){
            return ;
        }
        Ride r = lineRepo.findByLineName(reservation.getLineName()).getRides().stream()
                .filter(ride -> ride.getRideDate().getDate()==d.getDate())
                .filter(ride -> ride.getFlagAndata()==reservation.isFlagAndata())
                .findAny().orElse(null);
        reservationRepo.delete(reservation);
        if(r==null){
            return;
        }
        r.getReservations().remove(reservation_id);
    }
}

