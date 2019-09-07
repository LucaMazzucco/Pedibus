package it.polito.appinternet.pedibus.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mongodb.util.JSON;
import it.polito.appinternet.pedibus.model.Child;
import it.polito.appinternet.pedibus.model.Reservation;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.ChildRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildService {
    @Autowired
    ChildRepository childRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;


    public Child findByRegistrationNumber(String registrationNumber) {
        return childRepo.findByRegistrationNumber(registrationNumber);
    }

    public Child findById(String id){
        return childRepo.findById(id);
    }

    public List<Child> findAll(){
        return childRepo.findAll();
    }

    public JSONObject encapsulateChildOnRide(Child child, Boolean isPresent){
        JSONObject jsonChild = new JSONObject();
        jsonChild.put("name", child.getName());
        jsonChild.put("surname", child.getSurname());
        jsonChild.put("registrationNumber", child.getRegistrationNumber());
        jsonChild.put("isPresent",isPresent);
        return jsonChild;
    }

    public JSONObject encapsulateChildInfo(Child child){
        JSONObject jsonChild = new JSONObject();
        jsonChild.put("name",child.getName());
        jsonChild.put("surname", child.getSurname());
        jsonChild.put("registrationNumber", child.getRegistrationNumber());
        return jsonChild;
    }

    public JSONObject encapsulateReservations(String ssn){
        Child child = findByRegistrationNumber(ssn);
        if(child == null){
            return null;
        }
        User parent = userService.userFindById(child.getParentId());
        List<Reservation> resList = parent.getReservations().stream()
                .map(res->reservationService.findById(res))
                .filter(res->res.getChild().equals(child.getId()))
                .collect(Collectors.toList());
        return reservationService.encapsulateChildReservations(resList,child,parent);
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public boolean addChildReservations(String ssn, String payload){
        Child child = findByRegistrationNumber(ssn);
        List<Reservation> resList = new LinkedList<>();
        JSONObject jPayload = new JSONObject(payload);
        if(child == null) return false;
        if(!jPayload.has("reservations")) return false;
        JSONArray jResArr = jPayload.getJSONArray("reservations");
        for(int i=0;i<jResArr.length();i++){
            JSONObject jRes = (JSONObject) jResArr.get(i);
            List<Reservation> tmp = decapsulateChildReservation(child,jRes);
            if(tmp == null) return false;
            resList.addAll(tmp);
        }
        try{
            resList.forEach(r-> {
                r.setId("");
                long id = reservationService.addReservation(r);
                if(id<0){
                    throw new NullPointerException("error");
                }
                r.setId(String.valueOf(id));
            });
        }
        catch(NullPointerException e){
            e.printStackTrace();
            resList.forEach(r->{
                if(r.getId().length()>0){
                    reservationService.deleteReservation(r.getLineName(),r.getReservationDate(),r.getId());
                }
            });
            return false;
        }
        return true;
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public boolean addChildReservation(String ssn, String payload){
        JSONObject jPayload = new JSONObject(payload);
        Child child = findByRegistrationNumber(ssn);
        if(child==null) return false;
        List<Reservation> resList = decapsulateChildReservation(child,jPayload);
        try{
            resList.forEach(r-> {
                r.setId("");
                long id = reservationService.addReservation(r);
                if(id<0){
                    throw new NullPointerException("error");
                }
                r.setId(String.valueOf(id));
            });
        }
        catch(NullPointerException e){
            e.printStackTrace();
            resList.forEach(r->{
                if(r.getId().length()>0){
                    reservationService.deleteReservation(r.getLineName(),r.getReservationDate(),r.getId());
                }
            });
            return false;
        }
        return true;
    }

    @Transactional
    public boolean editChildReservation(String ssn, String payload){
        Child child = findByRegistrationNumber(ssn);
        if(child == null || payload.length()==0) return false;
        JSONObject jPayload = new JSONObject(payload);
        List<Reservation> resList = decapsulateChildReservation(child,jPayload);
        try{
            resList.forEach(r->{
                Reservation res = reservationService.findByLineNameAndReservationDateAndFlagGoingAndChild(r.getLineName(),r.getReservationDate(),r.isFlagGoing(),child.getId());
                if(res==null) throw new NullPointerException("Error, reservation not found.");
                String id = res.getId();
                if(reservationService.updateReservation(r.getLineName(),r.getReservationDate(),id,r)<0){
                    throw new NullPointerException("Error");
                }
            });
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteChildReservation(String ssn, String payload){
        JSONObject jPayload = new JSONObject(payload);
        Child child = findByRegistrationNumber(ssn);
        List<Reservation> resList = decapsulateChildReservation(child, jPayload);
        try{
            resList.forEach(r->{
                if(reservationService.deleteReservation(r)<0){
                    throw new NullPointerException("error");
                }
            });
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private List<Reservation> decapsulateChildReservation(Child child, JSONObject reservation){
        JSONObject jStop;
        Reservation r;
        List<Reservation> resList = new LinkedList<>();
        if(child == null) return null;
        if(!reservation.has("lineName")
            || !reservation.has("rideDate")
            || !reservation.has("child")
                || !reservation.has("parent")
        ) return null;
        if(!reservation.has("stopA")
                && !reservation.has("stopR")) return null;
        User parent = userService.userGet(reservation.getString("parent"));
        if(parent == null) return null;
        if(reservation.has("stopA")){
            jStop = reservation.getJSONObject("stopA");
            if(!jStop.has("stopName")
                || !jStop.has("time")) return null;
            r = new Reservation(reservation.getString("lineName"),
                    jStop.getString("stopName"),
                    child.getId(),
                    parent.getId(),
                    reservation.getLong("rideDate"),
                    true,
                    false
                    );
            resList.add(r);
        }
        if(reservation.has("stopR")){
            jStop = reservation.getJSONObject("stopR");
            if(!jStop.has("stopName")
                    || !jStop.has("time")) return null;
            r = new Reservation(reservation.getString("lineName"),
                    jStop.getString("stopName"),
                    child.getId(),
                    parent.getId(),
                    reservation.getLong("rideDate"),
                    false,
                    false
            );
            resList.add(r);
        }
        return resList;
    }
}
