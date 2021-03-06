package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.Roles;
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
import java.util.stream.Collectors;

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

    @GetMapping("/user/refresh/{email}")
    public ResponseEntity refreshToken(@PathVariable String email){
            return userService.generateNewToken(email);
    }

    @PostMapping("/register/{token}")
    public ResponseEntity<String> registerUser(@RequestBody String payload, @PathVariable String token)
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
        //ObjectMapper mapper = new ObjectMapper();

        String name = jsonInput.getString("name");
        String surname = jsonInput.getString("surname");
        String registrationNumber = jsonInput.getString("registrationNumber");
        String password = jsonInput.getString("password");
        String email = jsonInput.getString("email");

        String role = userService.getRoleFromToken(email);
        boolean isParent = false;

        if(role == null){
            return ResponseEntity.badRequest().body("No role specified");
        }

        if(role.equals("Genitore")){
            isParent = true;
        }

        User newUser = new User(email, password, name, surname, registrationNumber, true, isParent);

        if(userService.userRegister(newUser, role) == -1){
            jsonOutput.put("result", "User already exists!");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }
        else{
            jsonOutput.put("result", "You have been correctly registered!");
        }
        return ok(jsonOutput.toString());
    }

    @GetMapping("/user/{userEmail}/messages")
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

    @PutMapping("/user/{userEmail}/messages")
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

    @GetMapping("/user/unread/{userEmail}")
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

    @GetMapping("/user/confirm/{randomUUID}")
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

    @GetMapping("/user/users")
    public String getSystemUser(){
        List<String> usernames;
        usernames = userService.userGetNames();
        return new JSONArray(usernames).toString();
    }

    @GetMapping("/user/getUsersRoles")
    public ResponseEntity getUsersRoles(){
        JSONArray returnMap = new JSONArray();

        List<User> users = userService.getUsers().stream()
                .filter(u->u.getRoles().contains(Roles.ROLE_CONDUCTOR.getRole()))
                .collect(Collectors.toList());
        for (User user : users){
            JSONObject jsonOutput = new JSONObject();
            if(user.getRoles().contains(Roles.ROLE_CONDUCTOR.getRole())){
                if(user.getRoles().contains(Roles.ROLE_ADMIN.getRole())){
                    jsonOutput.put("role", "Amministratore");
                    jsonOutput.put("line", user.getAdminLines().get(0));
                } else {
                    jsonOutput.put("role", "Accompagnatore");
                }
                jsonOutput.put("email", user.getEmail());
                returnMap.put(jsonOutput);
            }
        };

        return ok(returnMap.toString());
    }

    @GetMapping("/user/getUsersByRole/{role}")
    public ResponseEntity<String> getUsersByRole(@PathVariable String role){
        if(role.length()==0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(userService.usersGetByRole(role).toString(),HttpStatus.OK);
    }

    @PutMapping("/user/users/{user_id}")
    public void enableUserAdmin(@PathVariable String user_id, @RequestBody String payload, ServletRequest req){
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("lineName") || !jsonInput.has("enable"))
            return;

        boolean enableAdmin = jsonInput.getBoolean("enable");
        String line_name = jsonInput.getString("lineName");
        userService.userEnableAdmin(user_id,req,line_name,enableAdmin);
    }

    @GetMapping("/user/checkEmail/{email}")
    public ResponseEntity<Boolean> checkEmailPresence(@PathVariable String email){
        return new ResponseEntity<>(userService.isUserPresent(email),HttpStatus.OK);
    }

    @PostMapping("/user/registerAdmin")
    public ResponseEntity registerAdmin(@RequestBody String payload){
        JSONObject jsonInput = new JSONObject(payload);
        JSONObject jsonOutput = new JSONObject();

        if(!jsonInput.has("email") || !jsonInput.has("role")){
            jsonOutput.put("result", "Wrong Request");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }

        String email = jsonInput.getString("email");
        String role = jsonInput.getString("role");
        String line = jsonInput.getString("line");

        if(userService.isUserPresent(email)){
            jsonOutput.put("result", "User already exists!");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }

        if(!userService.userRegisterByAdmin(email, role, line)){
            jsonOutput.put("result", "There has been some error during registration");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        } else {
            jsonOutput.put("result", "The new user has been correctly registered");
        }

        return ok(jsonOutput.toString());
    }

    @GetMapping("/user/checkToken/{email}/{token}")
    public ResponseEntity<Boolean> checkUserToken(@PathVariable String email, @PathVariable String token){
        return new ResponseEntity<>(userService.isTokenRight(email, token), HttpStatus.OK);
    }

    @GetMapping("/user/getChildren/{email}")
    public ResponseEntity<String> getChildren(@PathVariable String email){
        if(email.length()==0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        JSONArray jArray = userService.userGetChildren(email);
        if(jArray==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(jArray.toString(),HttpStatus.OK);
    }

    @PostMapping("/user/addChild/{email}")
    public ResponseEntity addChild(@PathVariable String email, @RequestBody String payload){
        if(payload.length()==0) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if(userService.userAddChild(email,payload)) return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/user/editChild/{email}")
    public ResponseEntity editChild(@PathVariable String email, @RequestBody String payload){
        if(payload.length()==0) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if(userService.userEditChild(email, payload)) return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/user/deleteChild/{email}")
    public ResponseEntity deleteChild(@PathVariable String email, @RequestBody String payload){
        if(payload.length()==0) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if(userService.userDeleteChild(email, payload)) return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/user/deleteRole")
    public ResponseEntity deleteRole(@RequestBody String payload){
        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("email") ||
                !jsonInput.has("line")){
            jsonOutput.put("result", "Wrong Request");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }

        String email = jsonInput.getString("email");
        String linename = jsonInput.getString("line");

        if(!userService.removeRoleAndLine(email, linename)){
            return ResponseEntity.badRequest().body("L'operazione non è andata a buon fine");
        }
        jsonOutput.put("email", email);
        jsonOutput.put("role", "Accompagnatore");
        jsonOutput.put("line", "");

        return ResponseEntity.ok(jsonOutput.toString());
    }

    @SuppressWarnings("Duplicates")
    @PostMapping("/user/addRole")
    public ResponseEntity addRole(@RequestBody String payload){
        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("email") ||
                !jsonInput.has("line")){
            jsonOutput.put("result", "Wrong Request");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }
        String email = jsonInput.getString("email");
        String linename = jsonInput.getString("line");
        if(!userService.addRoleAndLine(email, linename)){
            return ResponseEntity.badRequest().body("La promozione non è andata a buon fine");
        }
        jsonOutput.put("email", email);
        jsonOutput.put("line", linename);
        jsonOutput.put("role", "Amministratore");

        return ResponseEntity.ok(jsonOutput.toString());
    }
}
