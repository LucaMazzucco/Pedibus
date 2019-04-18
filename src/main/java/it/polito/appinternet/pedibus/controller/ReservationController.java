package it.polito.appinternet.pedibus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    PersonRepository personRepo;

    @Autowired
    LineRepository lineRepo;

    @Autowired
    StopRepository stopRepo;

    @PostConstruct
    public void init(){
        try {
            List<Reservation> newReservations = new LinkedList<>();
            List<Person> newPeople = new LinkedList<>();
            Stop stopA = null, stopR = null;
            String tmp_arrival, tmp_departure, tmp_date, tmp_line_name;
            Boolean tmp_back;
            Person tmp_person;
            InputStream is = new FileInputStream("src/main/data/lines.json");
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject root_obj = new JSONObject(jsonTxt);
            if(!root_obj.has("reservations")){
                throw new Exception("No reservations found");
            }
            JSONArray reservations_array = root_obj.getJSONArray("reservations");
            tmp_arrival=tmp_departure=null;
            for(int i = 0; i < reservations_array.length(); i++){
                stopA = null;
                stopR = null;
                JSONObject lineObj = reservations_array.getJSONObject(i);
                if(!lineObj.has("lineName") ||
                !lineObj.has("stopType") ||
                !lineObj.has("stopName") ||
                !lineObj.has("back") ||
                !lineObj.has("date") ||
                !lineObj.has("passengers")){
                    continue;
                }
                tmp_line_name = lineObj.getString("lineName");
                if(lineObj.getString("stopType").equals("a")){
                    tmp_departure = lineObj.getString("stopName");
                    stopA = stopRepo.findByStopName(tmp_departure);
                }
                else{
                    tmp_arrival = lineObj.getString("stopName");
                    stopR = stopRepo.findByStopName(tmp_arrival);
                }
                tmp_back = lineObj.getBoolean("back");
                tmp_date = lineObj.getString("date");
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date date = format.parse(tmp_date);
                JSONArray people_array = lineObj.getJSONArray("passengers");
                Line tmp_line = lineRepo.findByLineName(tmp_line_name);
                for(int j = 0; j < people_array.length(); j++){
                    JSONObject personObj = people_array.getJSONObject(j);
                    if(!personObj.has("firstName") ||
                    !personObj.has("lastName")||
                    !personObj.has("registrationNumber")){
                        continue;
                    }
                    String tmp_name = personObj.getString("firstname");
                    String tmp_last = personObj.getString("lastname");
                    String tmp_number = personObj.getString("registrationNumber");
                    tmp_person = personRepo.findByRegistrationNumber(tmp_number);
                    if(tmp_person == null){
                        tmp_person = new Person(tmp_name, tmp_last, tmp_number);
                        personRepo.save(tmp_person);
                    }
                    Reservation tmp_res = new Reservation(tmp_line, stopA , stopR, tmp_person, date, tmp_back);
                    newReservations.add(tmp_res);
                }
            }

            for(Reservation a : newReservations){
                insertLine(a);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/insertReservation")
    public String insertLine(Reservation reservation){
        reservationRepo.save(reservation);
        return "Line inserted correctly";
    }

    @GetMapping("/reservations/{line_name}/{date}")
    public String findByDateAndLine(@PathVariable String line_name, @PathVariable String date){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date tmp_date = format.parse(date);
            List<Reservation> f = reservationRepo.findByLine_LineNameAndReservationDate(line_name, tmp_date);
            List<Person> arr = new LinkedList<>();
            List<Person> dep = new LinkedList<>();
            Map<String, List<String>> personPerStopA= new HashMap<>();
            Map<String, List<String>> personPerStopR= new HashMap<>();
            List<Stop> stopsA = f.get(0).getLine().getStopListA();
            List<Stop> stopsR = f.get(0).getLine().getStopListR();
            for(Stop s : stopsA){
                personPerStopA.put(s.getStopName(), new LinkedList<>());
            }
            for(Stop s : stopsR){
                personPerStopR.put(s.getStopName(), new LinkedList<>());
            }
            for(Reservation r : f){
                if(r.getArrival() != null){
                    personPerStopA.get(r.getArrival().getStopName()).add(r.getPassenger().toString());
                    //arr.add(r.getPassenger());
                }
                else if(r.getDeparture() != null){
                    personPerStopR.get(r.getDeparture().getStopName()).add(r.getPassenger().toString());
                    //dep.add(r.getPassenger());
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

            //mainObj.put("stops", arr);
            //mainObj.put("outgoing", dep);
            return mainObj.toString();

        } catch (Exception e){
            e.printStackTrace();
        }
        return "niente";
    }

    @PostMapping("/reservations/{line_name}/{date}")
    public Long addReservation(@PathVariable String line_name, @PathVariable String date, @RequestBody String payload){
        Line line = lineRepo.findByLineName(line_name);
        JSONObject json = new JSONObject(payload);
        if(!json.has("stopType") ||
        !json.has("stop") ||
        !json.has("registrationNumber") ||
        !json.has("back")){
            return new Long(-1);
        }
        Stop aStop = null, rStop = null;
        Person p = personRepo.findByRegistrationNumber(json.getString("registrationNumber"));
        if(json.getString("stopType").equals("a")){
            aStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else if(json.getString("stopType").equals("r")){
            rStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else{
            return new Long(-2);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date tmp_date = format.parse(date);
            Reservation r = new Reservation(line, aStop, rStop, p, tmp_date, json.getBoolean("back"));
            Reservation inserted = reservationRepo.save(r);
            return inserted.getId();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Long(-3);
    }

    @PutMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public Long updateReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable Long reservation_id, @RequestBody String payload){
        Optional<Reservation> oR = reservationRepo.findById(reservation_id);
        Line line = lineRepo.findByLineName(line_name);
        if (!oR.isPresent() || line == null) {
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
        if (json.getString("stopType").equals("a")) {
            aStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else if(json.get("stopType").equals("r")){
            rStop = stopRepo.findByStopName(json.getString("stop"));
        }
        else{
            return new Long(-3);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date tmp_date = format.parse(date);
            r.setLine(line);
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

        return new Long(-4);
    }

    @DeleteMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public void deleteReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable Long reservation_id){
        Optional<Reservation> oR = reservationRepo.findById(reservation_id);
        if (!oR.isPresent()) {
            return;
        }

        Reservation r = oR.get();
        reservationRepo.delete(r);
    }

    @GetMapping("/reservations/{line_name}/{date}/{reservation_id}")
    public Reservation getReservation(@PathVariable String line_name, @PathVariable String date, @PathVariable Long reservation_id){
        Optional<Reservation> oR = reservationRepo.findById(reservation_id);
        if (!oR.isPresent()) {
            return null;
        }

        Reservation r = oR.get();
        return r;
    }

}

