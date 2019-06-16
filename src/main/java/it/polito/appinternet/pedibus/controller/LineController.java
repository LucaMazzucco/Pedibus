package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.swing.text.DateFormatter;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:4200/presenze", maxAge = 3600)
@RestController
public class LineController {

    @Autowired
    LineRepository lineRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ReservationRepository reservationRepo;


    @GetMapping("/insertLine")
    public String insertLine(Line l) {
        //TO DO: Controlli sulle stop della line da inserire e persone se presenti
        lineRepo.insert(l);
        return "Line inserted correctly";
    }


    @GetMapping("/lines")
    public String findAllLines(){
        List<Line> allLines = lineRepo.findAll();
        JSONArray lineNames = new JSONArray();
        for(Line line : allLines){
            lineNames.put(line.getLineName());
        }
        return lineNames.toString();
    }

    //Get json of all lines
    @GetMapping("/getLines")
    public String getLinesJson(){
        List<Line> allLines = lineRepo.findAll();
        JSONArray lines = new JSONArray();
        for(Line l:allLines){
            lines.put(encapsulateLine(l));
        }
        return lines.toString();
    }

    //Get json of a single line
    @GetMapping("/getLines/{line_name}")
    public String getLineJson(@PathVariable String line_name){
        Line line = lineRepo.findByLineName(line_name);
        if(line!=null){
            return encapsulateLine(line).toString();
        }
        return "";
    }

    //Get json of a single Ride A/R (frontend format)
    @GetMapping("/getLines/{line_name}/{date}")
    public String getRideJson(@PathVariable String line_name,
                          @PathVariable String date){
        JSONObject returnJson = new JSONObject();
        try{
            Line line = lineRepo.findByLineName(line_name);
            Date rDate = new Date(date);
            if(line==null){
                return "";
            }
            Ride ride = line.getRides().stream()
                    .filter(r->r.getRideDate().getDate()==rDate.getDate())
                    .findAny().orElse(null);
            if(ride==null){
                return "";
            }
            returnJson = encapsulateRide(ride,line);
        } catch (NullPointerException e){
            e.printStackTrace();
            return "";
        }
        return returnJson.toString();
    }

    private JSONObject encapsulateLine(Line line){
        JSONObject lineJson = new JSONObject();
        JSONArray ridesJson = new JSONArray();
        for(Ride r:line.getRides()){
            JSONObject rideJson = encapsulateRide(r,line);
            if(rideJson!=null){
                ridesJson.put(rideJson);
            }
        }
        lineJson.put("lineName",line.getLineName());
        lineJson.put("rides",ridesJson);
        return lineJson;
    }

    private JSONObject encapsulateRide(Ride r,Line line){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        JSONObject rideJson = new JSONObject();
        JSONArray stopsJson = new JSONArray();
        JSONArray stopsBackJson = new JSONArray();
        JSONArray notReserved = new JSONArray();
        JSONArray notReservedBack = new JSONArray();
        Ride r2 = line.getRides().stream()
                .filter(x->x.getRideDate().getDate()==r.getRideDate().getDate())
                .filter(x->x.getFlagAndata()!=r.getFlagAndata())
                .findFirst().orElse(null);
        if(!r.getFlagAndata()){
            if(r2!=null){
                return null;
            }
            else{
                stopsBackJson = encapsulateStops(r,line.getStopListR());
                notReservedBack = encapsulateNotReserved(r2);
            }
        }
        else{
            stopsJson = encapsulateStops(r,line.getStopListA());
            notReserved = encapsulateNotReserved(r);
            if(r2!=null){
                stopsBackJson = encapsulateStops(r2,line.getStopListA());
                notReservedBack = encapsulateNotReserved(r2);
            }
        }
        rideJson.put("date",sdf.format(r.getRideDate()));
        rideJson.put("stops",stopsJson);
        rideJson.put("stopsBack",stopsBackJson);
        rideJson.put("notReserved",notReserved);
        rideJson.put("notReservedBack",notReservedBack);
        return rideJson;
    }

    private JSONArray encapsulateStops(Ride ride,List<Stop> stops){
        JSONArray stopsJson = new JSONArray();
        HashMap<String,JSONObject> stopsJsonMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        stops.forEach(s->{
            JSONObject stopJson = new JSONObject();
            stopJson.put("stopName",s.getStopName());
            stopJson.put("time",sdf.format(s.getTime()));
            stopJson.put("people",new JSONArray());
            stopsJsonMap.put(s.getStopName(),stopJson);
        });
        ride.getReservations().stream()
                .map(e->reservationRepo.findById(e))
                .collect(Collectors.groupingBy(e->e.getStopName(),HashMap::new,Collectors.toList()))
                .forEach((k,v)->{
                    JSONArray people = new JSONArray();
                    v.forEach(e->people.put(
                            encapsulateUser(
                                    e.getPassenger(),e.isPresent()
                            )
                            )
                    );
                    if(stopsJsonMap.containsKey(k)){
                        stopsJsonMap.get(k).put("people",people);
                    }
                })
        ;
        stopsJsonMap.values().forEach(s->stopsJson.put(s));
        return stopsJson;
    }

    private JSONObject encapsulateUser(User user,Boolean isPresent){
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("name", user.getName());
        jsonUser.put("surname", user.getSurname());
        jsonUser.put("registrationNumber", user.getRegistrationNumber());
        jsonUser.put("isPresent",isPresent);
        return jsonUser;
    }

    private JSONArray encapsulateNotReserved(Ride ride){
        JSONArray notReserved = new JSONArray();
        userRepo.findAll().stream()
                .filter(u->u.getRoles().stream().noneMatch(s->s.equals("ROLE_ADMIN")))
                .filter(u->
                        ride.getReservations().stream()
                                .map(x -> reservationRepo.findById(x).getPassenger().getRegistrationNumber())
                                .noneMatch(x -> x.equals(u.getRegistrationNumber()))
                )
                .forEach(u->
                        notReserved.put(encapsulateUser(u,false))
                );
        return notReserved;
    }

    @PutMapping("/putLineAttendance/{line_name}")
    @Transactional
    public ResponseEntity<String> updateLineToUpdatePassengersInfo(@PathVariable String line_name, @RequestBody String payload){
        if(line_name==null || payload==null){
            return ResponseEntity.badRequest().build();
        }
        try{
            Line line = lineRepo.findByLineName(line_name);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
            List<Ride> rides = line.getRides();
            JSONObject lineJson = new JSONObject(payload).getJSONObject("line");
            JSONArray ridesJson = lineJson.getJSONArray("rides");
            List<Reservation> reservationsToSave = new LinkedList<>();
            List<Reservation> reservationsToInsert = new LinkedList<>();
            for(int i=0; i<ridesJson.length();i++){
                JSONObject rideJson = ridesJson.getJSONObject(i);
                Date date = sdf.parse(rideJson.getString("date"));
                Ride rideA = rides.stream()
                        .filter(r->r.getRideDate().getDate()==(date.getDate()))
                        .filter(Ride::getFlagAndata).findAny().orElse(null);
                Ride rideR = rides.stream()
                        .filter(r->r.getRideDate().getDate()==(date.getDate()))
                        .filter(r-> !r.getFlagAndata()).findAny().orElse(null);
                decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stops"),
                        rideA, reservationsToInsert, reservationsToSave);
                decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stopsBack"),
                        rideR, reservationsToInsert, reservationsToSave);
            }
            reservationsToSave.forEach(reservationRepo::save);
            reservationsToInsert.forEach(reservation->{
                Ride ride = line.getRides().stream()
                        .filter(r->r.getRideDate().getDate()==reservation.getReservationDate().getDate())
                        .filter(r->r.getFlagAndata()==reservation.isFlagAndata())
                        .findAny().orElse(null);
                Reservation res = reservationRepo.insert(reservation);
                ride.getReservations().add(res.getId());
                //Should always find a ride in the specified direction or the reservation in wrong
            });
            lineRepo.save(line);
        } catch (JSONException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (NullPointerException e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("");
    }

    @PutMapping("putLineAttendance/{line_name}/ride")
    @Transactional
    public ResponseEntity<String> updateRideToUpdatePassengersInfo(@PathVariable String line_name,
                                                                   @RequestBody String payload){
        if(line_name==null || payload == null){
            return ResponseEntity.badRequest().build();
        }
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
            Line line = lineRepo.findByLineName(line_name);
            List<Reservation> reservationsToSave = new LinkedList<>();
            List<Reservation> reservationsToInsert = new LinkedList<>();
            JSONObject rideJson = new JSONObject(payload).getJSONObject("ride");
            Date date = sdf.parse(rideJson.getString("date"));
            Ride rideA = line.getRides().stream()
                    .filter(r->r.getRideDate().getDate()==(date.getDate()))
                    .filter(r->r.getFlagAndata()==true).findAny().orElse(null);
            Ride rideR = line.getRides().stream()
                    .filter(r->r.getRideDate().getDate()==(date.getDate()))
                    .filter(r->r.getFlagAndata()==false).findAny().orElse(null);
            decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stops"),
                    rideA, reservationsToInsert,reservationsToSave);
            decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stopsBack"),
                    rideR, reservationsToInsert, reservationsToSave);
            reservationsToSave.forEach(reservationRepo::save);
            reservationsToInsert.forEach(reservation->{
                reservation = reservationRepo.insert(reservation);
                if(reservation.isFlagAndata()){
                    rideA.getReservations().add(reservation.getId());
                }
                else{
                    rideR.getReservations().add(reservation.getId());
                }
            });
            lineRepo.save(line);
        } catch (JSONException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (NullPointerException e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("");
    }

    private void decapsulateRideToUpdatePassengersInfo(String lineName, JSONArray stops, Ride ride,
                                                       List<Reservation> reservationsToInsert,
                                                       List<Reservation> reservationsToSave){
        if(ride == null){
            return;
        }
        for(int i=0;i<stops.length();i++){
            JSONObject stopJson = stops.getJSONObject(i);
            String stopName = stopJson.getString("stopName");
            if(stopName==null){
                throw new JSONException("stopName not found");
            }
            List<Reservation> reservations = ride.getReservations().stream()
                    .map(r->reservationRepo.findById(r))
                    .filter(r->r.getStopName().equals(stopName))
                    .collect(Collectors.toList());
            JSONArray peopleJson = stopJson.getJSONArray("people");
            for(int j=0;j<peopleJson.length();j++){
                JSONObject userJson = peopleJson.getJSONObject(j);
                String ssn = userJson.getString("registrationNumber");
                if(!userJson.has("registrationNumber") ||
                        !userJson.has("isPresent")){
                    throw new JSONException("passenger's info missing");
                }
                User user = userRepo.findByRegistrationNumber(ssn);
                boolean isPresent = userJson.getBoolean("isPresent");
                if(user==null){
                    throw new JSONException("passenger info are not correct");
                }
                Reservation res = reservations.stream()
                        .filter(r->r.getPassenger().getRegistrationNumber().equals(user.getRegistrationNumber()))
                        .findAny().orElse(null);
                if(res==null){
                    //Was not reserved but manually signed present
                    res = new Reservation(lineName,stopName,user,ride.getRideDate(),ride.getFlagAndata(),true);
                    reservationsToInsert.add(res);
                }
                else if(isPresent!=res.isPresent()){
                    res.setPresent(isPresent);
                    reservationsToSave.add(res);
                }
            }
        }
    }


    @GetMapping("/lines/{line_name}")
    public String findLineByName(@PathVariable String line_name){
        Line found = lineRepo.findByLineName(line_name);
        JSONArray stopA = new JSONArray();
        JSONArray stopR = new JSONArray();
        for(Stop s : found.getStopListA()){
            stopA.put(s.getStopName());
        }
        for(Stop s : found.getStopListR()){
            stopR.put(s.getStopName());
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("stopListA", stopA);
        mainObj.put("stopListR", stopR);
        return mainObj.toString();
    }

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Line[] newLines = mapper.readValue(new FileReader("./src/main/data/lines.json"), Line[].class);
            for(Line l : newLines){
                lineRepo.insert(l);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
