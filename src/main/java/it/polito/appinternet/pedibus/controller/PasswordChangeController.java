package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.PwdChangePost;
import it.polito.appinternet.pedibus.service.PasswordChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordChangeController {
    @Autowired
    PasswordChangeService pwdChgService;

    @GetMapping("/recover/{randomUUID}")
    public ResponseEntity showChangePassword(@PathVariable String randomUUID, @ModelAttribute("request")PwdChangePost pcp, Model m){
        m.addAttribute("token", randomUUID);
        if(pwdChgService.isTokenPresent(randomUUID)){
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/recover/{randomUUID}")
    public ResponseEntity changePassword(@PathVariable String randomUUID, @ModelAttribute("request") PwdChangePost pcp, Model m) {
        switch (pwdChgService.changePassword(randomUUID, pcp)){
            case 0:
                m.addAttribute("message", "Password changed successfully");
                return new ResponseEntity(HttpStatus.OK);
            case 1:
                m.addAttribute("message", "Your link has expired");
                return new ResponseEntity(HttpStatus.OK);
            case 2:
                m.addAttribute("message", "The two passwords doesn't match");
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
