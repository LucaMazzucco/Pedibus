package it.polito.appinternet.pedibus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.PwdChangeRequestRepository;
import it.polito.appinternet.pedibus.security.JwtTokenProvider;
import it.polito.appinternet.pedibus.service.EmailSenderService;
import it.polito.appinternet.pedibus.repository.ConfirmationTokenRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.ResponseEntity.ok;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private PwdChangeRequestRepository pwdChangeRequestRepo;

    @PostConstruct
    public void init(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            User[] newUsers = mapper.readValue(new FileReader("./src/main/data/persons.json"), User[].class);
            for(User a : newUsers){
                userRepo.insert(a);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody String payload){
        try {
            JSONObject json_input = new JSONObject(payload);
            if(!json_input.has("username") || !json_input.has("password")){
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            String username = json_input.getString("username");
            String password = json_input.getString("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepo.findByEmail(username).get();
            if(!user.isEnabled()){
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            String token = jwtTokenProvider.createToken(username, userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
            JSONObject res = new JSONObject();
            res.put("token", token);
            return ok(res.toString());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/recover")
    public ResponseEntity recoverPassword(@RequestBody String payload){
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("email")){
            return ResponseEntity.badRequest().body("Wrong JSON format");
        }
        String email = jsonInput.getString("email");
        Optional<User> existingUser = userRepo.findByEmail(email);
        if(!existingUser.isPresent()){
            return ResponseEntity.badRequest().body("No user associated with the given mail");
        }
        User u = existingUser.get();
        PwdChangeRequest pcr = new PwdChangeRequest(u);
        pwdChangeRequestRepo.insert(pcr);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("You have requested to change your password");
        mailMessage.setFrom("pedibus.polito1819@gmail.com");
        mailMessage.setText("To change your password click here : "
                +"http://localhost:8080/recover/"+pcr.getToken());

        emailSenderService.sendEmail(mailMessage);
        return ok().body("Mail sent");
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
        User existingUser = userRepo.findByRegistrationNumber(newUser.getRegistrationNumber());
        if(existingUser!=null){
            jsonOutput.put("result", "User already exists!");
            return ResponseEntity.badRequest().body(jsonOutput.toString());
        }
        else{
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            newUser.setEnabled(false);
            List<String> roles = new LinkedList<>();
            roles.add("ROLE_USER");
            newUser.setRoles(roles);
            userRepo.insert(newUser);

            ConfirmationToken confirmationToken = new ConfirmationToken(newUser);

            confirmationTokenRepository.insert(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(newUser.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("pedibus.polito1819@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    +"http://localhost:8080/confirm/" +confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);
            jsonOutput.put("result", "You have been correctly registered! Check your email");
            return ok(jsonOutput.toString());
        }
    }

    @GetMapping("/confirm/{randomUUID}")
    public ResponseEntity confirmUserAccount(@PathVariable String randomUUID)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(randomUUID);
        if(token != null)
        {
            Optional<User> user = userRepo.findByEmail(token.getUser().getEmail());
            if(user.isPresent()){
                user.get().setEnabled(true);
                userRepo.save(user.get());
                return new ResponseEntity(HttpStatus.OK);
            }
            else{
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public String getSystemUser(){
        List<User> allUsers;
        allUsers = userRepo.findAll();
        List<String> usernames = new LinkedList<>();
        for(User user: allUsers){
            usernames.add(user.getName());
        }
        return new JSONArray(usernames).toString();
    }

    @PutMapping("/users/{user_id}")
    public void enableUserAdmin(@PathVariable String user_id, @RequestBody String payload, ServletRequest req){
        Optional<User> opt_user = userRepo.findById(user_id);

        if(!opt_user.isPresent()){
            return;
        }
        User user = opt_user.get();
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        String username = jwtTokenProvider.getUsername(token);
        if(username == null)
            return;

        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("lineName") || !jsonInput.has("enable"))
            return;

        boolean enableAdmin = jsonInput.getBoolean("enable");
        String line_name = jsonInput.getString("lineName");
        Line line = lineRepository.findByLineName(line_name);

        if(line == null)
            return;

        if(enableAdmin){
            if(line.getAdmins().contains(user.getEmail()))
                return;

            if(user.getRoles().contains("ROLE_USER")){
                user.getRoles().remove("ROLE_USER");
                user.getRoles().add("ROLE_ADMIN");
            }

            user.getAdminLines().add(line_name);
            line.getAdmins().add(user.getEmail());
            userRepo.save(user);
            lineRepository.save(line);
            return;

        }

        if(user.getAdminLines().contains(line_name) && line.getAdmins().contains(user.getEmail())){
            user.getAdminLines().remove(line_name);
            line.getAdmins().remove(user.getEmail());

            if(user.getAdminLines().isEmpty() && user.getRoles().contains("ROLE_ADMIN")){
                user.getRoles().remove("ROLE_ADMIN");
                user.getRoles().add("ROLE_USER");
            }
            userRepo.save(user);
            lineRepository.save(line);
        }

    }

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<Boolean> checkEmailPresence(@PathVariable String email){
        JSONObject response = new JSONObject();
        response.put("res",userRepo.findByEmail(email).isPresent());
        return ok(userRepo.findByEmail(email).isPresent());
    }
}
