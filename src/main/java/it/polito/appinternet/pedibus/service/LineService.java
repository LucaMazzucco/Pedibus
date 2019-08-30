package it.polito.appinternet.pedibus.service;

import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.LineRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class LineService {
    @Autowired
    LineRepository lineRepo;
    @Autowired
    ChildService childService;
    @Autowired
    ReservationService reservationService;
    @Autowired
    UserService userService;


    public String insertLine(Line l) {
        //TO DO: Controlli sulle stop della line da inserire e persone se presenti
        l = lineRepo.insert(l);
        return "Line inserted correctly with id: " + l.getId();
    }

    public List<Line> findAll(){
        return lineRepo.findAll();
    }
    public List<String> findAllLinesNames() {
        return lineRepo.findAll().stream().map(l->l.getLineName()).collect(toList());
    }

    public Line findByLineName(String lineName){
        return lineRepo.findByLineName(lineName);
    }

    public void saveLine(Line line){
        lineRepo.save(line);
    }

    public Ride getRideByLineAndDate(Line line, String date){
        Date rDate = new Date(date);
        if(line==null){
            return null;
        }
        Ride ride = line.getRides().stream()
                .filter(r->r.getRideDate().getDate()==rDate.getDate())
                .findAny().orElse(null);
        return ride;
    }

    public JSONObject encapsulateLine(Line line) {
        JSONObject lineJson = new JSONObject();
        JSONArray ridesJson = new JSONArray();
        for (Ride r : line.getRides()) {
            JSONObject rideJson = encapsulateRide(r, line);
            if (rideJson != null) {
                ridesJson.put(rideJson);
            }
        }
        lineJson.put("lineName", line.getLineName());
        lineJson.put("rides", ridesJson);
        return lineJson;
    }

    public JSONObject encapsulateRide(Ride r,Line line){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        JSONObject rideJson = new JSONObject();
        JSONArray stopsJson = new JSONArray();
        JSONArray stopsBackJson = new JSONArray();
        JSONArray notReserved = new JSONArray();
        JSONArray notReservedBack = new JSONArray();
        Ride r2 = line.getRides().stream()
                .filter(x->x.getRideDate().getDate()==r.getRideDate().getDate())
                .filter(x->x.isFlagGoing()!=r.isFlagGoing())
                .findFirst().orElse(null);
        if(r.isFlagGoing()){
            stopsJson = encapsulateStops(r);
            notReserved = encapsulateNotReserved(r);
            if(r2!=null){
                stopsBackJson = encapsulateStops(r2);
                notReservedBack = encapsulateNotReserved(r2);
            }
        }
        else{
            if(r2!=null){
                return null;
            }
            else{
                stopsBackJson = encapsulateStops(r);
                notReservedBack = encapsulateNotReserved(r);
            }
        }
        rideJson.put("date",sdf.format(r.getRideDate()));
        rideJson.put("stops",stopsJson);
        rideJson.put("stopsBack",stopsBackJson);
        rideJson.put("notReserved",notReserved);
        rideJson.put("notReservedBack",notReservedBack);
        return rideJson;
    }

    private JSONArray encapsulateNotReserved(Ride ride){
        JSONArray notReserved = new JSONArray();
        List<String> childrenOnRide = ride.getStops().stream()
                .flatMap(s->s.getReservations().stream())
                .map(rId->reservationService.findById(rId).getChild())
                .collect(toList());
        childService.findAll().stream()
                .filter(c-> !childrenOnRide.contains(c.getId()))
                .map(c->childService.encapsulateChild(c,false))
                .forEach(cJson->{
                    notReserved.put(cJson);
                });
        return notReserved;
    }

    private JSONArray encapsulateStops(Ride ride){
        JSONArray stopsJson = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        ride.getStops().forEach(s->{
            JSONObject stopJson = new JSONObject();
            stopJson.put("stopName",s.getStopName());
            stopJson.put("time",sdf.format(s.getTime()));
            JSONArray children = new JSONArray();
            s.getReservations().stream()
                    .map(r->reservationService.findById(r))
                    .map(r->childService.encapsulateChild(childService.findById(r.getChild()),r.isPresent()))
                    .forEach(children::put);
            stopJson.put("children",children);
            stopsJson.put(stopJson);
        });
        return stopsJson;
    }

    @Transactional
    public int updateLineToUpdatePassengersInfo(String line_name, String payload){
        try{
            Line line = findByLineName(line_name);
            if(line==null){
                return -1;
            }
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
                        .filter(Ride::isFlagGoing).findAny().orElse(null);
                Ride rideR = rides.stream()
                        .filter(r->r.getRideDate().getDate()==(date.getDate()))
                        .filter(r-> !r.isFlagGoing()).findAny().orElse(null);
                decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stops"),
                        rideA, reservationsToInsert, reservationsToSave);
                decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stopsBack"),
                        rideR, reservationsToInsert, reservationsToSave);
            }
            reservationsToSave.forEach(reservationService::saveReservation);
            reservationsToInsert.forEach(reservation->{
                String resId = reservationService.insertReservation(reservation);
                Ride ride = line.getRides().stream()
                        .filter(r->r.getRideDate().getDate()==reservation.getReservationDate().getDate())
                        .filter(r->r.isFlagGoing()==reservation.isFlagGoing())
                        .findAny().orElse(null);
                Stop stop = ride.getStops().stream().filter(s->s.getStopName().equals(reservation.getStopName()))
                        .findAny().orElse(null);
                User parent = userService.userFindById(reservation.getParent());
                stop.getReservations().add(resId);
                parent.getReservations().add(resId);
                userService.userSave(parent);
                //Should always find a ride in the specified direction or the reservation in wrong
            });
            lineRepo.save(line);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        } catch (NullPointerException e){
            e.printStackTrace();
            return -2;
        } catch (ParseException e) {
            e.printStackTrace();
            return -3;
        }
        return 0;
    }

    @Transactional
    public int updateRideToUpdatePassengersInfo(String line_name, String payload){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
            Line line = findByLineName(line_name);
            List<Reservation> reservationsToSave = new LinkedList<>();
            List<Reservation> reservationsToInsert = new LinkedList<>();
            JSONObject rideJson = new JSONObject(payload).getJSONObject("ride");
            Date date = sdf.parse(rideJson.getString("date"));
            Ride rideA = line.getRides().stream()
                    .filter(r->r.getRideDate().getDate()==(date.getDate()))
                    .filter(r->r.isFlagGoing()==true)
                    .findAny().orElse(null);
            Ride rideR = line.getRides().stream()
                    .filter(r->r.getRideDate().getDate()==(date.getDate()))
                    .filter(r->r.isFlagGoing()==false)
                    .findAny().orElse(null);
            decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stops"),
                    rideA, reservationsToInsert,reservationsToSave);
            decapsulateRideToUpdatePassengersInfo(line_name,rideJson.getJSONArray("stopsBack"),
                    rideR, reservationsToInsert, reservationsToSave);
            reservationsToSave.forEach(reservationService::saveReservation);
            reservationsToInsert.forEach(reservation->{
                String resId = reservationService.insertReservation(reservation);
                User parent = userService.userFindById(reservation.getParent());
                parent.getReservations().add(resId);
                userService.userSave(parent);
                if(reservation.isFlagGoing()){
                    rideA.getStops().stream().filter(s->s.getStopName().equals(reservation.getStopName()))
                            .findAny().get()
                            .getReservations().add(resId);
                }
                else{
                    rideR.getStops().stream().filter(s->s.getStopName().equals(reservation.getStopName()))
                            .findAny().get()
                            .getReservations().add(resId);
                }
            });
            lineRepo.save(line);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return -1;
        } catch (NullPointerException e){
            e.printStackTrace();
            return -2;
        }
        return 0;
    }

    @Transactional
    public int addNewAvailability(Shift availability){
        try{
            Line line = findByLineName(availability.getLineName());
            Ride ride = line.getRides().stream().filter(r -> r.getRideDate().equals(availability.getRideDate())).findAny().orElse(null);
            List<String> companions = new LinkedList<>();
            companions.add(availability.getEmail());
            ride.setCompanions(companions);
            ride.setConfirmed(false);
            lineRepo.save(line);
            return 0;
        }
        catch (NullPointerException ne){
            ne.printStackTrace();
            return -1;
        }
    }

    public int sendMessageLineAdmin(String lineName, String sender){
        Line line = findByLineName(lineName);
        List<String> admins = line.getAdmins();
        if(admins.isEmpty()){
            return -1;
        }


        Message m = new Message(Instant.now().getEpochSecond(), false, "L'accompagnatore " + sender + " è disponibile per la linea " + lineName );
        admins.forEach(a -> {
            User u = userService.userGet(a);
            u.getMessages().add(m);
            userService.userSave(u);
        });

        return 0;

    }
    @Transactional
    public int updateUserToUpdatePassengersInfo(String line_name, String dateString, String registrationNumber,
                                                boolean isPresent, boolean isFlagGoing) {
        try {
            Line line = findByLineName(line_name);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(dateString);
            Child child = childService.findByRegistrationNumber(registrationNumber);
            List<Reservation> reservations = reservationService.findByLineNameAndReservationDateAndFlagAndata(line_name, date, isFlagGoing);
            Reservation reservation = reservations.stream()
                    .filter(r->r.getChild().equals(child.getId()))
                    .findAny().orElse(null);
            if(reservation==null){
                return -1;
            }
            if(reservation.isPresent()!=isPresent){
                reservation.setPresent(isPresent);
                reservationService.saveReservation(reservation);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            return -2;
        } catch (ParseException e) {
            e.printStackTrace();
            return -3;
        }
        return 0;
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
            List<Reservation> reservations = ride.getStops().stream()
                    .filter(s->s.getStopName().equals(stopName))
                    .flatMap(s->s.getReservations().stream())
                    .map(r->reservationService.findById(r))
                    .collect(Collectors.toList());
            JSONArray peopleJson = stopJson.getJSONArray("children");
            for(int j=0;j<peopleJson.length();j++){
                JSONObject childrenJson = peopleJson.getJSONObject(j);
                String ssn = childrenJson.getString("registrationNumber");
                if(!childrenJson.has("registrationNumber") ||
                        !childrenJson.has("isPresent")){
                    throw new JSONException("passenger's info missing");
                }
                Child child = childService.findByRegistrationNumber(ssn);
                boolean isPresent = childrenJson.getBoolean("isPresent");
                if(child==null){
                    throw new JSONException("passenger info are not correct");
                }
                Reservation res = reservations.stream()
                        .filter(r->r.getChild().equals(child.getId()))
                        .findAny().orElse(null);
                if(res==null){
                    //Was not reserved but manually added
                    res = new Reservation(lineName,stopName,child.getId(),child.getParentId(),ride.getRideDate(),ride.isFlagGoing(),isPresent);
                    reservationsToInsert.add(res);
                }
                else if(isPresent!=res.isPresent()){
                    res.setPresent(isPresent);
                    reservationsToSave.add(res);
                }
            }
        }
        if(reservationsToInsert.stream()
                .filter(r->r.isFlagGoing()==ride.isFlagGoing())
                .collect(Collectors.groupingBy(Reservation::getChild,Collectors.counting()))
                .values().stream()
                .anyMatch(v->v>1) ||
                reservationsToSave.stream()
                        .filter(r->r.isFlagGoing()==ride.isFlagGoing())
                        .collect(Collectors.groupingBy(Reservation::getChild,Collectors.counting()))
                        .values().stream()
                        .anyMatch(v->v>1)
        ){
            //Duplicated passenger in json
            throw new JSONException("Duplicated passenger in json");
        }
    }

    public List<Line> getLineOfCompanions(String companion){
        return lineRepo.findByRides_Companions(companion);
    }

}
