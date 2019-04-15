package it.polito.appinternet.pedibus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polito.appinternet.pedibus.model.Person;
import it.polito.appinternet.pedibus.model.Reservation;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.ReservationRepository;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    LineRepository lineRepo;

    @PostConstruct
    public void init(){
        try {
            List<Reservation> newReservations = new LinkedList<>();
            List<Person> newPeople = new LinkedList<>();
            String tmp_arrival, tmp_departure, tmp_date, tmp_line_name;
            InputStream is = new FileInputStream("src/main/data/lines.json");
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject root_obj = new JSONObject(jsonTxt);
            JSONArray reservations_array = root_obj.getJSONArray("reservations");
            for(int i = 0; i < reservations_array.length(); i++){
                JSONObject lineObj = reservations_array.getJSONObject(i);
                tmp_line_name = lineObj.getString("lineName");
                tmp_departure = lineObj.getString("departureName");
                tmp_arrival = lineObj.getString("arrivalName");
                tmp_date = lineObj.getString("date");
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date date = format.parse(tmp_date);
                JSONArray people_array = lineObj.getJSONArray("passengers");
                Stop arrival = new Stop(tmp_arrival);
                Stop departure = new Stop(tmp_departure);
                Line tmp_line = lineRepo.findByLineName(tmp_line_name);
                for(int j = 0; j < people_array.length(); j++){

                    JSONObject personObj = people_array.getJSONObject(j);
                    String tmp_name = personObj.getString("firstname");
                    String tmp_last = personObj.getString("lastname");
                    String tmp_number = personObj.getString("registrationNumber");

                    Person tmp_person = new Person(tmp_name, tmp_last, tmp_number);

                    newPeople.add(tmp_person);

                    Reservation tmp_res = new Reservation(tmp_line, arrival, departure, tmp_person, date);
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
            return f.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "niente";
    }

}
