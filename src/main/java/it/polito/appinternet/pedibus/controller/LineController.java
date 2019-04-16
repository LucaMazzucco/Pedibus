package it.polito.appinternet.pedibus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polito.appinternet.pedibus.model.Stop;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.StopRepository;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    LineRepository lineRepo;

    @Autowired
    StopRepository stopRepo;

    @PostConstruct
    public void init() {
        try {
            //FileReader reader = new FileReader("src/main/data/lines.json");
            /*
            Gson gsonLines = new Gson();
            Line[] newLines = gsonLines.fromJson(reader, Line[].class);
            for(Line a : newLines){
                insertLine(a);
            }*/

            List<Line> newLines = new LinkedList<>();
            String tmp_name, tmp_stop_name;
            InputStream is = new FileInputStream("src/main/data/lines.json");
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject root_obj = new JSONObject(jsonTxt);
            JSONArray lines_array = root_obj.getJSONArray("lines");
            Stop stop_tmp;
            for(int i = 0; i < lines_array.length(); i++){
                List<Stop> stopA_tmp = new LinkedList<>();
                List<Stop> stopB_tmp = new LinkedList<>();
                JSONObject lineObj = lines_array.getJSONObject(i);
                tmp_name = lineObj.getString("lineName");
                JSONArray stopA_array = lineObj.getJSONArray("stopListA");
                for(int iA = 0; iA < stopA_array.length(); iA++){
                    JSONObject stopAObj = stopA_array.getJSONObject(iA);
                    tmp_stop_name = stopAObj.getString("stopName");
                    stop_tmp = stopRepo.findByStopName(tmp_stop_name);
                    if(stop_tmp == null){
                        stop_tmp = new Stop(tmp_stop_name);
                        stopRepo.save(stop_tmp);
                    }
                    stopA_tmp.add(stop_tmp);
                }
                JSONArray stopB_array = lineObj.getJSONArray("stopListR");
                for(int iB = 0; iB < stopB_array.length(); iB++){
                    JSONObject stopBObj = stopB_array.getJSONObject(iB);
                    tmp_stop_name = stopBObj.getString("stopName");
                    stop_tmp = stopRepo.findByStopName(tmp_stop_name);
                    if(stop_tmp == null){
                        //Stop tmp_s = stopA_tmp.stream().filter(x->x.getStopName().equals(tmp_stop_name)).findAny().orElse(null);
                        //if(tmp_s ==null){
                        stop_tmp = new Stop(tmp_stop_name);
                        stopRepo.save(stop_tmp);
                        //}
                        //else{
                        //    stop_tmp = tmp_s;
                        //}
                    }
                    stopB_tmp.add(stop_tmp);
                }
                newLines.add(new Line(tmp_name, stopA_tmp, stopB_tmp));
            }
            for(Line a : newLines){
                insertLine(a);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/insertLine")
    public String insertLine(Line l){
        lineRepo.save(l);
        return "Line inserted correctly";
    }

    @GetMapping("/lines")
    public String findAllLines(){
        List<Line> allLines = lineRepo.findAll();
        JsonArray lineNames = new JsonArray();
        for(Line line : allLines){
            lineNames.add(line.getLineName());
        }
        return lineNames.toString();
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

}
