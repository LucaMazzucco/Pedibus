package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.ConfirmationToken;
import it.polito.appinternet.pedibus.service.EmailSenderService;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.ConfirmationTokenRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;


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
        User existingUser = userRepository.findByEmail(email);
        if(existingUser != null){
            return "User already exists!";
        }
        else{
            //TODO: validate password e email
            if(!password.equals(passwordConfirm)){
                return "The two password must be the same!";
            }
            User u = new User(email,password,false);
            userRepository.insert(u);

            ConfirmationToken confirmationToken = new ConfirmationToken(u);

            confirmationTokenRepository.insert(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("pedibus.polito1819@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    +"http://localhost:8080/confirm-account?token="+confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);
            return "You have been correctly registered! Check your email";
        }
    }

    @GetMapping("/confirm-account")
    public String confirmUserAccount(@RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        String message;
        if(token != null)
        {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            message = "Your account has been successfully confirmed";
        }
        else
        {
            message = "The link is invalid or broken!";
        }

        return message;
    }
}
