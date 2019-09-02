package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.Message;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostConstruct
    public void init(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            User[] newUsers = mapper.readValue(new FileReader("./src/main/data/persons.json"), User[].class);
            for(User a : newUsers){
                userService.userInsert(a);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody String payload){
        String username, password;
        JSONObject json_input = new JSONObject(payload);
        if(!json_input.has("username") || !json_input.has("password")){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        username = json_input.getString("username");
        password = json_input.getString("password");
        return userService.userLogin(username,password);
    }

    @PostMapping("/recover")
    public ResponseEntity recoverPassword(@RequestBody String payload){
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("email")){
            return ResponseEntity.badRequest().body("Wrong JSON format");
        }
        String email = jsonInput.getString("email");
        return userService.userRecoverPassword(email);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody String payload)
    {
        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("email") ||
                !jsonInput.has("name") ||
                !jsonInput.has("surname") ||
                !jsonInput.has("registrationNumber") ||
                !jsonInput.has("password")){
            jsonOutput.put("result", "Wrong Request");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }
        ObjectMapper mapper = new ObjectMapper();
        User newUser = null;
        try {
            newUser = mapper.readValue(payload, User.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(newUser == null){
            jsonOutput.put("result", "Input Error!!");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }
        else if(userService.userRegister(newUser) == -1){
            jsonOutput.put("result", "User already exists!");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }
        else{
            jsonOutput.put("result", "You have been correctly registered! Check your email");
        }
        return ok(jsonOutput.toString());
    }

    @GetMapping("/{userEmail}/messages")
    public ResponseEntity getUserMessages(@PathVariable String userEmail){
        if(userEmail==null || userEmail.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Message> messages = userService.userGetMessages(userEmail);
        if(messages!=null){
            JSONArray result = new JSONArray(messages);
            return ok(result.toString());
        }
        else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userEmail}/messages")
    public ResponseEntity putMessages(@PathVariable String userEmail, @RequestBody String payload){
        ObjectMapper mapper = new ObjectMapper();
        List<Message> newMsg = null;
        try {
            newMsg = Arrays.asList(mapper.readValue(payload, Message[].class));
        } catch (IOException e){
            e.printStackTrace();
        }
        if(newMsg == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(!userService.userPutMessages(userEmail,newMsg)){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/unread/{userEmail}")
    public ResponseEntity getUnreadMessagesCount(@PathVariable String userEmail){
        if(userEmail.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        long count = userService.userGetUnreadMessagesCount(userEmail);
        if(count<0){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return ok(count);
    }

    @GetMapping("/confirm/{randomUUID}")
    public ResponseEntity confirmUserAccount(@PathVariable String randomUUID)
    {
        if(randomUUID.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        else if(userService.userConfirmAccount(randomUUID)==0){
            return new ResponseEntity(HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public String getSystemUser(){
        List<String> usernames;
        usernames = userService.userGetNames();
        return new JSONArray(usernames).toString();
    }

    @PutMapping("/users/{user_id}")
    public void enableUserAdmin(@PathVariable String user_id, @RequestBody String payload, ServletRequest req){
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("lineName") || !jsonInput.has("enable"))
            return;

        boolean enableAdmin = jsonInput.getBoolean("enable");
        String line_name = jsonInput.getString("lineName");
        userService.userEnableAdmin(user_id,req,line_name,enableAdmin);
    }

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<Boolean> checkEmailPresence(@PathVariable String email){
        return new ResponseEntity<>(userService.isUserPresent(email),HttpStatus.OK);
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity registerAdmin(@RequestBody String payload){
        JSONObject jsonInput = new JSONObject(payload);
        JSONObject jsonOutput = new JSONObject();

        if(!jsonInput.has("email") || !jsonInput.has("role")){
            jsonOutput.put("result", "Wrong Request");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }

        String email = jsonInput.getString("email");
        String role = jsonInput.getString("role");

        if(userService.isUserPresent(email)){
            jsonOutput.put("result", "User already exists!");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }

        if(!userService.userRegisterByAdmin(email, role)){
            jsonOutput.put("result", "There has been some error during registration");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        } else {
            jsonOutput.put("result", "The new user has been correctly registered");
        }

        return ok(jsonOutput.toString());
    }

    @GetMapping("checkToken/{email}/{token}")
    public ResponseEntity<Boolean> checkUserToken(@PathVariable String email, @PathVariable String token){
        return new ResponseEntity<>(userService.isTokenRight(email, token), HttpStatus.OK);
    }
}
