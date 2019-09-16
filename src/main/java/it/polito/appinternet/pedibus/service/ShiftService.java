package it.polito.appinternet.pedibus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;
import it.polito.appinternet.pedibus.model.Shift;
import it.polito.appinternet.pedibus.repository.ShiftRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class ShiftService {
    @Autowired
    ShiftRepository shiftRepository;

    public Shift findById(String id){
        return shiftRepository.findById(id).get();
    }
    public List<Shift> findByEmailAndLineNameAndFlagGoing(String email, String lineName, boolean flagGoing){
        return shiftRepository.findByEmailAndLineNameAndFlagGoing(email,lineName,flagGoing);
    }

    public List<Shift> findByEmail(String email){
        return new LinkedList<>(shiftRepository.findByEmail(email));
    }

    @Transactional
    public Shift insertShift(Shift shift){
        if(shift == null) return null;
        if(shift.getId() != null ||
            shift.getEmail() == null ||
                shift.getRideDate() < 0 ||
                shift.getLineName() == null
        ) return null;
        if(shiftRepository.findByEmailAndLineNameAndRideDateAndFlagGoing(shift.getEmail(),shift.getLineName(),shift.getRideDate(),shift.isFlagGoing())!=null)
            return null;
        return shiftRepository.insert(shift);
    }

    @Transactional
    public Shift saveShift(Shift shift){
        if(shift == null) return null;
        if(shift.getId() == null ||
                shift.getEmail() == null ||
                shift.getRideDate() < 0 ||
                shift.getLineName() == null
        ) return null;
        if(findById(shift.getId()) == null)
            return null;
        return shiftRepository.save(shift);
    }

    @Transactional
    public boolean deleteShift(Shift shift){
        if(shift == null) return false;
        if(shift.getId() == null) return false;
        if(findById(shift.getId()) == null)
            return false;
        shiftRepository.delete(shift);
        return true;
    }

    public Shift decapsulateShift(JSONObject jShift){
        if(!jShift.has("email") ||
                !jShift.has("lineName") ||
                !jShift.has("rideDate") ||
                !jShift.has("flagGoing") ||
                !jShift.has("confirmed1") ||
                !jShift.has("confirmed2")
        ){
            return null;
        }
        Shift shift = new Shift(jShift.getString("email"),
                jShift.getString("lineName"),
                jShift.getLong("rideDate"),
                jShift.getBoolean("flagGoing"),
                jShift.getBoolean("confirmed1"),
                jShift.getBoolean("confirmed2")
        );
        Shift oldShift = shiftRepository
                .findByEmailAndLineNameAndRideDateAndFlagGoing(shift.getEmail(),shift.getLineName(),shift.getRideDate(),shift.isFlagGoing());
        if(oldShift != null){
            shift.setId(oldShift.getId());
        }
        return shift;
    }

    public JSONObject encapsulateShift(Shift shift){
        JSONObject jShift = new JSONObject();
        jShift.put("lineName",shift.getLineName());
        jShift.put("rideDate",shift.getRideDate());
        jShift.put("email",shift.getEmail());
        jShift.put("flagGoing",shift.isFlagGoing());
        jShift.put("confirmed1", shift.isConfirmed1());
        jShift.put("confirmed2", shift.isConfirmed2());
        return jShift;
    }
}
