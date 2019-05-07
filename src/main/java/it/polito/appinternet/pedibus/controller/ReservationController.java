package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.Person;
import it.polito.appinternet.pedibus.model.Reservation;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.PersonRepository;
import it.polito.appinternet.pedibus.repository.ReservationRepository;
import it.polito.appinternet.pedibus.repository.StopRepository;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import it.polito.appinternet.pedibus.model.Line;

import javax.annotation.PostConstruct;
import javax.swing.text.html.Option;
import java.io.*;
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
    PersonRepository personRepo;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Reservation[] newRes = mapper.readValue(new FileReader("src/main/data/reservation.json"), Reservation[].class);
            for(Reservation r : newRes){
                reservationRepo.save(r);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/insertReservation")
    public String insertLine(Reservation reservation){
        reservationRepo.save(reservation);
        return "Line inserted correctly";
    }

    @SuppressWarnings("Duplicates")
    @GetMapping("/reservations/{line_name}/{date}")
    public String findByDateAndLine(@PathVariable String line_name, @PathVariable String date){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date tmp_date = format.parse(date);
            List<Reservation> res_andata = reservationRepo.findByFlagAndataTrueAndLineNameAndAndReservationDate(line_name, tmp_date);
            List<Reservation> res_ritorno = reservationRepo.findByFlagAndataFalseAndLineNameAndAndReservationDate(line_name, tmp_date);
            Map<String, List<Person>> personPerStopA= new HashMap<>();
            Map<String, List<Person>> personPerStopR= new HashMap<>();
            for(Reservation r : res_andata){
                if(personPerStopA.containsKey(r.getStopName())){
                    personPerStopA.get(r.getStopName()).add(r.getPassenger());
                }
                else {
                    List<Person> l = new LinkedList<>();
                    l.add(r.getPassenger());
                    personPerStopA.put(r.getStopName(), l);
                }
            }
            for(Reservation r : res_ritorno){
                if(personPerStopR.containsKey(r.getStopName())){
                    personPerStopR.get(r.getStopName()).add(r.getPassenger());
                }
                else {
                    List<Person> l = new LinkedList<>();
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
/*
    @PostMapping("/reservations/{line_name}/{date}")
    public Long addReservation(@PathVariable String line_name, @PathVariable String date, @RequestBody String payload){
        Line lineName = lineRepo.findByLineName(line_name);
        JSONObject json = new JSONObject(payload);
        if(!json.has("stopType") ||
        !json.has("stop") ||
        !json.has("registrationNumber") ||
        !json.has("back") ||
        lineName==null){
            return new Long(-1);
        }
        Stop aStop = null, rStop = null;
        Person p = personRepo.findByRegistrationNumber(json.getString("registrationNumber"));
        String stopName = json.getString("stop");
        if(json.getString("stopType").equals("a")){
            aStop = lineName.getStopListA().stream().filter(s->s.getStopName().equals(stopName)).findAny().orElse(null);
            //aStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else if(json.getString("stopType").equals("r")){
            rStop = lineName.getStopListR().stream().filter(s->s.getStopName().equals(stopName)).findAny().orElse(null);
            //rStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else{
            return new Long(-2);
        }
        if(aStop == rStop){ //both equals null but stopType is correct--> Stop not found inside the given lineName
            return new Long(-3);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date tmp_date = format.parse(date);
            Reservation r = new Reservation(lineName, aStop, rStop, p, tmp_date, json.getBoolean("back"));
            Reservation inserted = reservationRepo.save(r);
            return inserted.getId();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Long(-4);
    }
 */
/*
    @PutMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public Long updateReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable Long reservation_id, @RequestBody String payload){
        Optional<Reservation> oR = reservationRepo.findById(reservation_id);
        Line lineName = lineRepo.findByLineName(line_name);
        if (!oR.isPresent() || lineName == null) {
            return new Long(-1);
        }
        Reservation r = oR.get();
        JSONObject json = new JSONObject(payload);
        if(!json.has("stopType") ||
                !json.has("stop") ||
                !json.has("registrationNumber") ||
                !json.has("back")){
            return new Long(-2);
        }
        Stop aStop = null, rStop = null;
        Person p = personRepo.findByRegistrationNumber(json.getString("registrationNumber"));
//        if (json.getString("stopType").equals("a")) {
//            aStop = stopRepo.findByStopName(json.getString("stop"));
//        }
//        else if(json.get("stopType").equals("r")){
//            rStop = stopRepo.findByStopName(json.getString("stop"));
//        }
//        else{
//            return new Long(-3);
//        }
        String stopName = json.getString("stop");
        if(json.getString("stopType").equals("a")){
            aStop = lineName.getStopListA().stream().filter(s->s.getStopName().equals(stopName)).findAny().orElse(null);
            //aStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else if(json.getString("stopType").equals("r")){
            rStop = lineName.getStopListR().stream().filter(s->s.getStopName().equals(stopName)).findAny().orElse(null);
            //rStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else{
            return new Long(-3);
        }
        if(aStop == rStop){ //both equals null but stopType is correct--> Stop not found inside the given lineName
            return new Long(-4);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date tmp_date = format.parse(date);
            r.setLineName(lineName);
            r.setArrival(aStop);
            r.setDeparture(rStop);
            r.setBack(json.getBoolean("back"));
            r.setPassenger(p);
            r.setReservationDate(tmp_date);
            Reservation inserted = reservationRepo.save(r);
            return inserted.getId();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Long(-5);
    }

    @DeleteMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public void deleteReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable Long reservation_id){
        Optional<Reservation> oR = reservationRepo.findById(reservation_id);
        if (!oR.isPresent()) {
            return;
        }

        Reservation r = oR.get();

        if(!r.getLineName().getLineName().equals(line_name) || !r.getReservationDate().toString().equals(date)){
            return;
        }

        reservationRepo.delete(r);
    }

    @GetMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public Reservation getReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable Long reservation_id){
        Optional<Reservation> oR = reservationRepo.findById(reservation_id);
        if (!oR.isPresent()) {
            return null;
        }

        Reservation r = oR.get();

        if(!r.getLineName().getLineName().equals(line_name) || !r.getReservationDate().toString().equals(date)){
            return null;
        }

        return r;
    }
    */
}

