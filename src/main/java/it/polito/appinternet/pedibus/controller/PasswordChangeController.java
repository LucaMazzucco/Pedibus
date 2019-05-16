package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.PwdChangePost;
import it.polito.appinternet.pedibus.model.PwdChangeRequest;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.PwdChangeRequestRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import it.polito.appinternet.pedibus.service.EmailSenderService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@Controller
public class PasswordChangeController {

    @Autowired
    private PwdChangeRequestRepository pwdChangeRequestRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/recover/{randomUUID}")
    public ResponseEntity showChangePassword(@PathVariable String randomUUID, @ModelAttribute("request")PwdChangePost pcp, Model m){
        m.addAttribute("token", randomUUID);
        PwdChangeRequest pcr = pwdChangeRequestRepo.findByToken(randomUUID);
        if(pcr != null){
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/recover/{randomUUID}")
    public ResponseEntity changePassword(@PathVariable String randomUUID, @ModelAttribute("request") PwdChangePost pcp, Model m) {
        String pass1 = pcp.getPass1();
        String pass2 = pcp.getPass2();
        PwdChangeRequest pcr = pwdChangeRequestRepo.findByToken(randomUUID);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = cal.getTime();
        if(pass1.equals(pass2)) {
            if(!pcr.getExpirationDate().before(utcDate)){
                m.addAttribute("message", "Your link has expired");
            }
            m.addAttribute("message", "Password changed successfully");
            Optional<User> optU = userRepository.findByEmail(pcr.getUser().getEmail());
            User u = optU.get();
            u.setPassword(passwordEncoder.encode(pass1));
            userRepository.save(u);
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            m.addAttribute("message", "The two password doesn't match");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
