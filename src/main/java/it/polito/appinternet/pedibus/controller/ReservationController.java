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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import it.polito.appinternet.pedibus.model.Line;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
            JSONArray reservations_array = root_obj.getJSONArray("reservations");
            tmp_arrival=tmp_departure=null;
            for(int i = 0; i < reservations_array.length(); i++){
                JSONObject lineObj = reservations_array.getJSONObject(i);
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
            for(Reservation r : f){
                if(r.getArrival() != null){
                    arr.add(r.getPassenger());
                }
                else if(r.getDeparture() != null){
                    dep.add(r.getPassenger());
                }
            }
            Gson gson = new Gson();
            gson.toJson(arr);
            gson.toJson(dep);
            return gson.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "niente";
    }
    /*
    @PostMapping("/reservations/{line_name}/{data}")
    public Long addReservation(@PathVariable String line_name, @PathVariable String date){

    }*/

}
