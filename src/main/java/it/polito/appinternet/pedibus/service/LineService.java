package it.polito.appinternet.pedibus.service;

import it.polito.appinternet.pedibus.Utils;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.LineRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class LineService {
    @Autowired
    private LineRepository lineRepo;
    @Autowired
    private ChildService childService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShiftService shiftService;

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

    public Line saveLine(Line line){
        return lineRepo.save(line);
    }

    public Ride getRideByLineAndDateAndFlagGoing(Line line, long rDate, boolean flagGoing){
        if(line==null){
            return null;
        }
        return line.getRides().stream()
                .filter(r->Utils.myCompareUnixDate(r.getRideDate(),rDate)==0 && r.isFlagGoing()==flagGoing)
                .findAny().orElse(null);
    }

    public Stop getStopByLineNameAndRideDateAndFlagGoingAndStopName(String lineName, long rDate, boolean flagGoing, String stopName){
        Line line = findByLineName(lineName);
        if(line==null) return null;
        Ride ride = getRideByLineAndDateAndFlagGoing(line, rDate, flagGoing);
        if(ride==null) return null;
        Stop stop = ride.getStops().stream()
                .filter(s->s.getStopName().equals(stopName))
                .findAny().orElse(null);
        return stop;
    }

    public List<Stop> getStopsByLineNameAndRideDateAndFlagGoing(String lineName, long rDate, boolean flagGoing){
        Line line = findByLineName(lineName);
        if(line==null) return null;
        Ride ride = getRideByLineAndDateAndFlagGoing(line, rDate, flagGoing);
        if(ride==null) return null;
        return ride.getStops();
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
        JSONObject rideJson = new JSONObject();
        JSONArray stopsJson = new JSONArray();
        JSONArray stopsBackJson = new JSONArray();
        JSONArray notReserved = new JSONArray();
        JSONArray notReservedBack = new JSONArray();
        Ride r2 = line.getRides().stream()
                .filter(x->Utils.myCompareUnixDate(x.getRideDate(),r.getRideDate())==0)
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
        rideJson.put("date",r.getRideDate());
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
                .map(c->childService.encapsulateChildOnRide(c,false))
                .forEach(cJson->{
                    notReserved.put(cJson);
                });
        return notReserved;
    }

    private JSONArray encapsulateStops(Ride ride){
        JSONArray stopsJson = new JSONArray();
        ride.getStops().forEach(s->{
            JSONObject stopJson = new JSONObject();
            stopJson.put("stopName",s.getStopName());
            stopJson.put("time",s.getTime());
            JSONArray children = new JSONArray();
            s.getReservations().stream()
                    .map(r->reservationService.findById(r))
                    .map(r->childService.encapsulateChildOnRide(childService.findById(r.getChild()),r.isPresent()))
                    .forEach(children::put);
            stopJson.put("children",children);
            if(s.getGeoJsonPoint() != null){
                stopJson.put("coordinates",s.getGeoJsonPoint().getCoordinates().toString());
            }
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
            List<Ride> rides = line.getRides();
            JSONObject lineJson = new JSONObject(payload).getJSONObject("line");
            JSONArray ridesJson = lineJson.getJSONArray("rides");
            List<Reservation> reservationsToSave = new LinkedList<>();
            List<Reservation> reservationsToInsert = new LinkedList<>();
            for(int i=0; i<ridesJson.length();i++){
                JSONObject rideJson = ridesJson.getJSONObject(i);
                long date = rideJson.getLong("date");
                Ride rideA = rides.stream()
                        .filter(r->Utils.myCompareUnixDate(r.getRideDate(),date)==0)
                        .filter(Ride::isFlagGoing).findAny().orElse(null);
                Ride rideR = rides.stream()
                        .filter(r->Utils.myCompareUnixDate(r.getRideDate(),date)==0)
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
                        .filter(r->Utils.myCompareUnixDate(r.getRideDate(),reservation.getReservationDate())==0)
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
        }
        return 0;
    }

    @Transactional
    public int updateRideToUpdatePassengersInfo(String line_name, String payload){
        try{
            Line line = findByLineName(line_name);
            List<Reservation> reservationsToSave = new LinkedList<>();
            List<Reservation> reservationsToInsert = new LinkedList<>();
            JSONObject rideJson = new JSONObject(payload).getJSONObject("ride");
            long date = rideJson.getLong("date");
            Ride rideA = line.getRides().stream()
                    .filter(r->Utils.myCompareUnixDate(r.getRideDate(),date)==0)
                    .filter(r->r.isFlagGoing()==true)
                    .findAny().orElse(null);
            Ride rideR = line.getRides().stream()
                    .filter(r->Utils.myCompareUnixDate(r.getRideDate(),date)==0)
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
        } catch (JSONException e) {
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
            //TODO: Sostituire con una query più complessa, non avevo voglia di farlo per colpa delle date
            Line line = findByLineName(availability.getLineName());
            Ride ride = getRideByLineAndDateAndFlagGoing(line,availability.getRideDate(),availability.isFlagGoing());
            if(ride == null)
                throw new NullPointerException("not found ride");
            ride.getCompanions().add(availability.getEmail());
            // ride.setConfirmed(false);
            saveLine(line);
            return 0;
        }
        catch (NullPointerException ne){
            ne.printStackTrace();
            return -1;
        }
    }

    @Transactional
    public int addNewShift(Shift shift, boolean confirmed){
        try {
            Line line = findByLineName(shift.getLineName());
            Ride ride = getRideByLineAndDateAndFlagGoing(line,shift.getRideDate(),shift.isFlagGoing());
            if(ride == null)
                throw new NullPointerException("not found ride");
            String user = shift.getEmail();
            if(ride.getOfficialCompanion()!=null && ride.getOfficialCompanion().equals(user) && ride.isConfirmed()){
                return -2;
            }
            else if(ride.isConfirmed()){
                return -3;
            }
            ride.setOfficialCompanion(user);
            ride.setConfirmed(confirmed);
            shift.setConfirmed2(true);
            shiftService.saveShift(shift);
            saveLine(line);
            return 0;
        }
        catch (NullPointerException ne) {
            ne.printStackTrace();
            return -1;
        }
    }

    @Transactional
    public int sendMessageLineAdmin(String lineName, Message m){
        Line line = findByLineName(lineName);
        List<String> admins = line.getAdmins();
        if(admins.isEmpty()){
            return -1;
        }
        admins.forEach(a -> {
            User u = userService.userFindByEmail(a);
            u.getMessages().add(m);
            userService.userSave(u);
        });
        return 0;
    }

    @Transactional
    public int sendMessageToUser(String email, Message m){
        User u = userService.userFindByEmail(email);
        if(u == null){
            return -1;
        }
        u.getMessages().add(m);
        userService.userSave(u);
        return 0;
    }

    @Transactional
    public int updateUserToUpdatePassengersInfo(String line_name, long dateUnix, String registrationNumber,
                                                boolean isPresent, boolean isFlagGoing) {
        try {
            Child child = childService.findByRegistrationNumber(registrationNumber);
            Reservation reservation = reservationService.findByLineNameAndReservationDateAndFlagGoingAndChild(line_name, dateUnix, isFlagGoing, child.getId());;
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

    @Transactional
    public int deleteAvailability(Shift av){
        try{
            Line line = findByLineName(av.getLineName());
            Ride ride = line.getRides().stream().filter(r -> {
                if(Utils.myCompareUnixDate(r.getRideDate(),av.getRideDate())==0
                        && r.getCompanions().contains(av.getEmail())
                        && r.isFlagGoing() == av.isFlagGoing()){
                    return true;
                }
                else{
                    return false;
                }
            }).findAny().orElseThrow(NullPointerException::new);
            ride.getCompanions().remove(av.getEmail());
            lineRepo.save(line);
            return 0;
        }
        catch (NullPointerException ne){
            ne.printStackTrace();
            return -1;
        }
    }

    public List<Line> findNoAdminLines(){
        return lineRepo.findAll().stream()
                .filter(l->l.getLineAdmins().isEmpty())
                .collect(Collectors.toList());
    }

    public List<Line> getAdministratedLines(String email){
        return lineRepo.findByLineAdmins(email);
    }

    public JSONArray getStopNamesByLineName(String lineName){
        Line line = findByLineName(lineName);
        JSONArray jsonArray = new JSONArray();
        if(line==null) return null;
        line.getRides().stream()
                .flatMap(r->r.getStops().stream())
                .map(Stop::getStopName)
                .distinct()
                .forEach(jsonArray::put);
        return jsonArray;
    }

    @Transactional
    public boolean deleteShift(Shift toDel){
        try{
            Line line = findByLineName(toDel.getLineName());
            Ride ride = line.getRides().stream().filter(r -> Utils.myCompareUnixDate(r.getRideDate(),toDel.getRideDate())==0
                    && r.getOfficialCompanion().contains(toDel.getEmail())
                    && r.isFlagGoing() == toDel.isFlagGoing()
            ).findAny().orElseThrow(NullPointerException::new);
            ride.setOfficialCompanion(null);
            ride.getCompanions().remove(toDel.getEmail());
            lineRepo.save(line);
            return true;
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

}
