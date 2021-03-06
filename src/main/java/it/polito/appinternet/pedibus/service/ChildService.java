package it.polito.appinternet.pedibus.service;

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
    private ChildRepository childRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private LineService lineService;

    public Child findByRegistrationNumber(String registrationNumber) {
        return childRepo.findByRegistrationNumber(registrationNumber);
    }

    public Child findById(String id){
        return childRepo.findById(id);
    }

    public List<Child> findAll(){
        return childRepo.findAll();
    }

    @Transactional
    public Child insertChild(Child child){
        if(child==null) return null;
        if(findByRegistrationNumber(child.getRegistrationNumber())!=null) return null;
        return childRepo.insert(child);
    }

    @Transactional
    public boolean saveChild(Child child){
        if(findById(child.getId())==null) return false;
        childRepo.save(child);
        return true;
    }

    @Transactional
    public boolean deleteChild(Child child){
        if(findById(child.getId())==null) return false;
        User parent = userService.userFindById(child.getParentId());
        if(parent != null){
            parent.getReservations().stream()
                    .map(reservationService::findById)
                    .filter(r->r.getChild().equals(child.getId()))
                    .forEach(reservationService::deleteReservation);
            parent.getChildren().remove(child.getId());
            userService.userSave(parent);
        }
        else{
            reservationService.findByChildId(child.getId())
                    .forEach(reservationService::deleteReservation);
        }
        childRepo.delete(child);
        return true;
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
        jsonChild.put("defaultLine", child.getDefaultLine());
        jsonChild.put("defaultStop", child.getDefaultStop());
        jsonChild.put("availableStops", lineService.getStopsNamesByLineNameAndFlagGoing(child.getDefaultLine(), true));
        return jsonChild;
    }

    /**
     * returns child in db if present, creates a child if not found in db (it does not insert it), returns null if json is wrong
     * @param jChild
     * @return
     */
    public Child decapsulateChildInfo(JSONObject jChild){
        if(!jChild.has("name")
                || !jChild.has("surname")
                || !jChild.has("registrationNumber")
        ) return null;
        Child child = findByRegistrationNumber(jChild.getString("registrationNumber"));
        if(child!=null){
            child.setName(jChild.getString("name"));
            child.setSurname(jChild.getString("surname"));
            if(jChild.has("defaultLine")) child.setDefaultLine(jChild.getString("defaultLine"));
            if(jChild.has("defaultStop")) child.setDefaultStop(jChild.getString("defaultStop"));
            return child;
        }
        child = new Child(jChild.getString("name"),
                jChild.getString("surname"),
                jChild.getString("registrationNumber"),
                "");
        if(jChild.has("defaultLine")) child.setDefaultLine(jChild.getString("defaultLine"));
        if(jChild.has("defaultStop")) child.setDefaultStop(jChild.getString("defaultStop"));
        return child;
    }

    public JSONArray encapsulateReservations(String ssn){
        Child child = findByRegistrationNumber(ssn);
        if(child == null){
            return null;
        }
        User parent = userService.userFindById(child.getParentId());
        if(parent==null) return null;
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
                r = reservationService.addReservation(r);
                if(r == null){
                    throw new NullPointerException("error");
                }
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
        List<Reservation> resList = new LinkedList<>();
        List<Reservation> decap = decapsulateChildReservation(child, jPayload);
        if(decap == null) return false;
        resList.addAll(decap);
        try{
            resList.forEach(r-> {
                r = reservationService.addReservation(r);
                if(r == null){
                    throw new NullPointerException("error");
                }
            });
        }
        catch(NullPointerException e){
            e.printStackTrace();
            resList.forEach(r->{
                if(r.getId() != null){
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
        List<Reservation> resList = new LinkedList<>();
        List<Reservation> decap = decapsulateChildReservation(child, jPayload);
        if(decap == null) return false;
        resList.addAll(decap);
        try{
            resList.forEach(r->{
                if(reservationService.updateReservation(r.getLineName(),r.getReservationDate(),r.getId(),r)<0){
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
        List<Reservation> resList = new LinkedList<>();
        resList.addAll(decapsulateChildReservation(child, jPayload));
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
        User parent = userService.userFindByEmail(reservation.getString("parent"));
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
            Reservation oldRes = reservationService.findByLineNameAndReservationDateAndFlagGoingAndChild(r.getLineName(),
                    r.getReservationDate(),
                    r.isFlagGoing(),
                    r.getChild());
            if(oldRes!=null)
                r.setId(oldRes.getId());
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
            Reservation oldRes = reservationService.findByLineNameAndReservationDateAndFlagGoingAndChild(r.getLineName(),
                    r.getReservationDate(),
                    r.isFlagGoing(),
                    r.getChild());
            if(oldRes!=null)
                r.setId(oldRes.getId());
            resList.add(r);
        }
        return resList;
    }
}
