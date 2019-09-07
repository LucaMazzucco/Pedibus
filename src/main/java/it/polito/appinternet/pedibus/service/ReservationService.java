package it.polito.appinternet.pedibus.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.polito.appinternet.pedibus.Utils;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.ReservationRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepo;
    @Autowired
    LineService lineService;
    @Autowired
    ChildService childService;
    @Autowired
    UserService userService;

    public Reservation findById(String reservation_id){
        return reservationRepo.findById(reservation_id);
    }

    /**
     * Manual insert to repo: no insert on parent or stop is done here.
     * @param reservation
     * @return reservationId
     */
    public String insertReservation(Reservation reservation){
        reservation = reservationRepo.insert(reservation);
        return reservation.getId();
    }
    public void saveReservation(Reservation reservation){
        reservationRepo.save(reservation);
    }

    public Reservation findByDateAndLineAndId(String line_name, long date, String reservation_id){
        Reservation reservation = reservationRepo.findByIdAndLineNameAndReservationDate(reservation_id,line_name,date);
        if(reservation!=null){
            return reservation;
        }
        else{
            return null;
        }

    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public Long addReservation(String line_name, long date,
                               String registrationNumber, String stopName,
                               boolean flagGoing, boolean isPresent){
        // To Test use addReservation.json
        Line l = lineService.findByLineName(line_name);
        Child child = childService.findByRegistrationNumber(registrationNumber);
        if(child==null){
            return new Long(-2);
        }
        User parent = userService.userFindById(child.getParentId());
        Stop s;
        if(reservationRepo.findByLineNameAndReservationDateAndFlagGoingAndChild(line_name, date, flagGoing, child.getId())!=null){
            return new Long(-3);
        }
        if(flagGoing){
            s = l.getRides().stream().filter(r->r.isFlagGoing())
                    .filter(r-> Utils.myCompareUnixDate(r.getRideDate(),date)==0)
                    .flatMap(r->r.getStops().stream())
                    .filter(x->x.getStopName().equals(stopName))
                    .findAny().orElse(null);
        }
        else{
            s = l.getRides().stream().filter(r->!r.isFlagGoing())
                    .filter(r-> Utils.myCompareUnixDate(r.getRideDate(),date)==0)
                    .flatMap(r->r.getStops().stream())
                    .filter(x->x.getStopName().equals(stopName))
                    .findAny().orElse(null);
        }
        if(s==null || parent==null || l==null){
            return new Long(-4);
        }
        Reservation r = new Reservation(line_name, stopName, child.getId(), child.getParentId(), date, flagGoing, isPresent);
        r = reservationRepo.insert(r); //insert crea un nuovo id
        s.getReservations().add(r.getId());
        parent.getReservations().add(r.getId());
        userService.userSave(parent);
        lineService.saveLine(l);
        return Long.getLong(r.getId());
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public Long updateReservation(String line_name, long date, String reservation_id, Reservation newRes){
        Reservation r = reservationRepo.findById(reservation_id);
        Line line = lineService.findByLineName(line_name);
        if (r == null || line == null || newRes == null) {
            return new Long(-1);
        }
        newRes.setId(r.getId());
        Child child = childService.findById(newRes.getChild());
        User parent = userService.userFindById(newRes.getParent());
        if(child == null || parent == null || !child.getParentId().equals(parent.getId())){
            return new Long(-2);
        }
        boolean flag = newRes.isFlagGoing();
        Stop stop = line.getRides().stream()
                .filter(ride -> Utils.myCompareUnixDate(ride.getRideDate(),date)==0)
                .filter(ride->ride.isFlagGoing()==flag)
                .flatMap(ride->ride.getStops().stream())
                .filter(s->s.getStopName().equals(r.getStopName()))
                .findAny().orElse(null);
        if(stop == null){
            return new Long(-4);
        }
        reservationRepo.save(newRes); //Save sovrascrive sull'id selezionato
        //No need to update stop because there is no id change
        return Long.valueOf(1);
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public Long deleteReservation(@PathVariable String line_name, @PathVariable long date, @PathVariable String reservation_id){
        Reservation reservation = reservationRepo.findById(reservation_id);
        if (reservation == null){
            return new Long(-1);
        }
        if(!reservation.getLineName().equals(line_name)
                || Utils.myCompareUnixDate(reservation.getReservationDate(),date)!=0){
            return new Long(-3);
        }
        Line line = lineService.findByLineName(reservation.getLineName());
        if(line==null){
            return new Long(-4);
        }
        Stop stop = line.getRides().stream()
                .filter(ride -> Utils.myCompareUnixDate(ride.getRideDate(),date)==0)
                .filter(ride->ride.isFlagGoing()==reservation.isFlagGoing())
                .flatMap(ride->ride.getStops().stream())
                .filter(s->s.getStopName().equals(reservation.getStopName()))
                .findAny().orElse(null);
        if(stop==null){
            return new Long(-5);
        }
        User parent = userService.userFindById(reservation.getParent());
        parent.getReservations().remove(reservation_id);
        stop.getReservations().remove(reservation_id);
        userService.userSave(parent);
        reservationRepo.delete(reservation);
        lineService.saveLine(line);
        return new Long(0);
    }

    public List<Reservation> findByLineNameAndReservationDateAndFlagGoing(String line_name, long date, boolean isFlagGoing) {
        return reservationRepo.findByLineNameAndReservationDateAndFlagGoing(line_name,date,isFlagGoing);
    }

    @SuppressWarnings("Duplicates")
    public JSONObject encapsulateChildReservations(List<Reservation> resList,Child child, User parent) {
        JSONObject resJson = new JSONObject();
        JSONArray resArray = new JSONArray();
        List<Reservation> backRes = resList.stream()
                .filter(r->!r.isFlagGoing())
                .collect(Collectors.toList());
        resList.stream()
                .filter(res->res.isFlagGoing())
                .forEach(res->{
                    JSONObject tmpJson = new JSONObject();
                    Stop stop = lineService.getStopByLineNameAndRideDateAndStopName(res.getLineName(),res.getReservationDate(),res.getStopName());
                    tmpJson.put("lineName",res.getLineName());
                    tmpJson.put("child",child.getRegistrationNumber());
                    tmpJson.put("parent",parent.getEmail());
                    tmpJson.put("rideDate",res.getReservationDate());
                    JSONObject stopA = new JSONObject();
                    stopA.put("stopName",res.getStopName());
                    stopA.put("time",stop.getTime());
                    tmpJson.put("stopA",stopA);
                    Reservation resB = backRes.stream()
                            .filter(r->Utils.myCompareUnixDate(r.getReservationDate(),res.getReservationDate())==0)
                            .findAny().get();
                    if(resB!=null){
                        JSONObject stopR = new JSONObject();
                        stop = lineService.getStopByLineNameAndRideDateAndStopName(res.getLineName(),res.getReservationDate(),resB.getStopName());
                        stopR.put("stopName",resB.getStopName());
                        stopR.put("time",stop.getTime());
                        tmpJson.put("stopR",stopR);
                        backRes.remove(resB);
                    }
                    resArray.put(tmpJson);
                });
        backRes.stream()
                .forEach(res->{
                    JSONObject tmpJson = new JSONObject();
                    Stop stop = lineService.getStopByLineNameAndRideDateAndStopName(res.getLineName(),res.getReservationDate(),res.getStopName());
                    JSONObject stopR = new JSONObject();
                    tmpJson.put("lineName",res.getLineName());
                    tmpJson.put("child",child.getRegistrationNumber());
                    tmpJson.put("parent",parent.getEmail());
                    tmpJson.put("rideDate",res.getReservationDate());
                    stopR.put("stopName",res.getStopName());
                    stopR.put("time",stop.getTime());
                    tmpJson.put("stopR",stopR);
                    resArray.put(tmpJson);
                });
        resJson.put("reservations",resArray);
        return resJson;
    }

//    @SuppressWarnings("Duplicates")
//    @GetMapping("/reservations/{line_name}/{date}")
//    public String findByDateAndLine(String line_name, String date){
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//            format.setTimeZone(TimeZone.getTimeZone("UTC"));
//            Date tmp_date = format.parse(date);
//            List<Reservation> res_andata = reservationRepo.findByLineNameAndReservationDateAndFlagGoingIsTrue(line_name, tmp_date);
//            List<Reservation> res_ritorno = reservationRepo.findByLineNameAndReservationDateAndFlagGoingIsFalse(line_name, tmp_date);
//            Map<String, List<User>> personPerStopA= new HashMap<>();
//            Map<String, List<User>> personPerStopR= new HashMap<>();
//            for(Reservation r : res_andata){
//                if(personPerStopA.containsKey(r.getStopName())){
//                    personPerStopA.get(r.getStopName()).add(r.getChild());
//                }
//                else {
//                    List<User> l = new LinkedList<>();
//                    l.add(r.getPassenger());
//                    personPerStopA.put(r.getStopName(), l);
//                }
//            }
//            for(Reservation r : res_ritorno){
//                if(personPerStopR.containsKey(r.getStopName())){
//                    personPerStopR.get(r.getStopName()).add(r.getPassenger());
//                }
//                else {
//                    List<User> l = new LinkedList<>();
//                    l.add(r.getPassenger());
//                    personPerStopR.put(r.getStopName(), l);
//                }
//            }
//            JSONObject mainObj = new JSONObject();
//            JSONObject jsonDataA = new JSONObject();
//            JSONObject jsonDataR = new JSONObject();
//            for(String key : personPerStopA.keySet()){
//                jsonDataA.put(key, personPerStopA.get(key).toString());
//            }
//            mainObj.put("stopsA", jsonDataA);
//            for(String key : personPerStopR.keySet()){
//                jsonDataR.put(key, personPerStopR.get(key).toString());
//            }
//            mainObj.put("stopsR", jsonDataR);
//            return mainObj.toString();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return "niente";
//    }
}
