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
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Date;

@RestController
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepo;

    @Autowired
    LineRepository lineRepo;

    @Autowired
    PersonRepository personRepo;

    @Autowired
    StopRepository stopRepo;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Reservation[] newRes = mapper.readValue(new FileReader("src/main/data/reservation.json"), Reservation[].class);
            for(Reservation r : newRes){
                reservationRepo.insert(r);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/insertReservation")
    public String insertLine(Reservation reservation){
        reservationRepo.insert(reservation);
        return "Reservation inserted correctly";
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
            !main_json.has("passenger")
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
        Person p = personRepo.findByRegistrationNumber(p_json.getString("registrationNumber"));
        String stopName = main_json.getString("stopName");
        Stop s = stopRepo.findByStopName(stopName);
        Boolean flagAndata = main_json.getBoolean("flagAndata");
        if(s==null || p==null || flagAndata==null || l==null){
            return new Long(-4);
        }
        Reservation r = new Reservation(line_name,stopName,p,tmp_date,flagAndata);
        r = reservationRepo.insert(r);//crea un nuovo id
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
                !mainJson.has("passenger")){
            return new Long(-2);
        }
        JSONObject pJson = mainJson.getJSONObject("passenger");
        if(!pJson.has("registrationNumber") ||
                !pJson.has("name") ||
                !pJson.has("surname")
        ){
            return new Long(-3);
        }

        reservationRepo.save(newRes); //Save sovrascrive sull'id selezionato

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
        reservationRepo.delete(reservation);
    }
}

