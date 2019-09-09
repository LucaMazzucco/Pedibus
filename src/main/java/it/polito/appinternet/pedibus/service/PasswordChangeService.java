package it.polito.appinternet.pedibus.service;

import it.polito.appinternet.pedibus.model.PwdChangePost;
import it.polito.appinternet.pedibus.model.PwdChangeRequest;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.PwdChangeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class PasswordChangeService {
    @Autowired
    private PwdChangeRequestRepository pwdChangeRequestRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;

    public boolean isTokenPresent(String randomUUID){
        PwdChangeRequest pcr = pwdChangeRequestRepo.findByToken(randomUUID);
        return pcr != null;
    }

    public int changePassword(String randomUUID, PwdChangePost pcp) {
        String pass1 = pcp.getPass1();
        String pass2 = pcp.getPass2();
        PwdChangeRequest pcr = pwdChangeRequestRepo.findByToken(randomUUID);
        if(pcr==null){
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = cal.getTime();
        if(pass1.equals(pass2)) {
            if(!pcr.getExpirationDate().before(utcDate)){
                return 1;
            }
            User user = userService.userFindByEmail(pcr.getUser().getEmail());
            if(user==null){
                return -2;
            }
            user.setPassword(passwordEncoder.encode(pass1));
            userService.userSave(user);
            return 0;
        }
        else{
            return 2;
        }
    }
}
