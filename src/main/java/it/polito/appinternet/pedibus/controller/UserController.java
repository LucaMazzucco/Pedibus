package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.ConfirmationToken;
import it.polito.appinternet.pedibus.model.PwdChangePost;
import it.polito.appinternet.pedibus.model.PwdChangeRequest;
import it.polito.appinternet.pedibus.repository.PwdChangeRequestRepository;
import it.polito.appinternet.pedibus.security.JwtTokenProvider;
import it.polito.appinternet.pedibus.service.EmailSenderService;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.ConfirmationTokenRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

import java.util.*;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody String payload){
        try {
            JSONObject json_input = new JSONObject(payload);
            if(!json_input.has("username") || !json_input.has("password")){
                return ResponseEntity.badRequest().body("Wrong JSON format");
            }
            String username = json_input.getString("username");
            String password = json_input.getString("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = jwtTokenProvider.createToken(username, userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
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
        Optional<User> existingUser = userRepository.findByEmail(email);
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
    public String registerUser(@RequestBody String payload)
    {
        JSONObject jsonInput = new JSONObject(payload);
        if(!jsonInput.has("email") ||
            !jsonInput.has("password") ||
            !jsonInput.has("passwordConfirm")){
            return "Wrong request";
        }
        String email = jsonInput.getString("email");
        String password = jsonInput.getString("password");
        String passwordConfirm = jsonInput.getString("passwordConfirm");
        Optional<User> existingUser = userRepository.findByEmail(email);
        if(existingUser.isPresent()){
            return "User already exists!";
        }
        else{
            //TODO: validate password e email
            if(!password.equals(passwordConfirm)){
                return "The two password must be the same!";
            }
            User u = new User(email,passwordEncoder.encode(password),false);
            userRepository.insert(u);

            ConfirmationToken confirmationToken = new ConfirmationToken(u);

            confirmationTokenRepository.insert(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("pedibus.polito1819@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    +"http://localhost:8080/confirm/" +confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);
            return "You have been correctly registered! Check your email";
        }
    }

    @GetMapping("/confirm/{randomUUID}")
    public String confirmUserAccount(@PathVariable String randomUUID)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(randomUUID);
        String message;
        if(token != null)
        {
            Optional<User> user = userRepository.findByEmail(token.getUser().getEmail());
            if(user.isPresent()){
                user.get().setEnabled(true);
                userRepository.save(user.get());
                message = "Your account has been successfully confirmed";
            }
            else{
                message = "Your account doesn't exists";
            }
        }
        else
        {
            message = "The link is invalid or broken!";
        }

        return message;
    }


}
