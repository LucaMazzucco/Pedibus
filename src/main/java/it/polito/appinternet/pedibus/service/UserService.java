package it.polito.appinternet.pedibus.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.Nullable;
import it.polito.appinternet.pedibus.model.*;
import it.polito.appinternet.pedibus.repository.ConfirmationTokenRepository;
import it.polito.appinternet.pedibus.repository.LineRepository;
import it.polito.appinternet.pedibus.repository.PwdChangeRequestRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import it.polito.appinternet.pedibus.security.JwtTokenProvider;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
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

    @Transactional
    public void userInsert(User user){
        userRepo.insert(user);
    }

    @Transactional
    public void userSave(User user){ userRepo.save(user); }

    @Transactional
    public void userRemove(User user){ userRepo.delete(user); }

    public User userGet(String email){
        return userRepo.findByEmail(email).get();
    }
    public User userFindById(String id){ return userRepo.findById(id).get();}

    public ResponseEntity userLogin(String username,String password){
        Optional<User> userOptional= userRepo.findByEmail(username);
        if(!userOptional.isPresent()){
            return new ResponseEntity("Invalid username/password supplied",HttpStatus.NOT_FOUND);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException ae){
            throw new BadCredentialsException("Invalid username/password supplied");
        }
        User user = userOptional.get();
        if(!user.isEnabled()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        String token = jwtTokenProvider.createToken(username, userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
        JSONObject res = new JSONObject();
        res.put("token", token);
        res.put("email", user.getEmail());
        return new ResponseEntity<>(res.toString(),HttpStatus.OK);
    }

    public boolean isUserPresent(String email){
        Optional<User> existingUser = userRepo.findByEmail(email);
        return existingUser.isPresent();
    }

    @Transactional
    public ResponseEntity userRecoverPassword(String email){
        User user = userRepo.findByEmail(email).get();
        if(user==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        PwdChangeRequest pcr = new PwdChangeRequest(user);
        pwdChangeRequestRepo.insert(pcr);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("You have requested to change your password");
        mailMessage.setFrom("pedibus.polito1819@gmail.com");
        mailMessage.setText("To change your password click here : "
                +"http://localhost:8080/recover/"+pcr.getToken());
        emailSenderService.sendEmail(mailMessage);
        return new ResponseEntity<>("Mail sent",HttpStatus.OK);
    }

    @Transactional
    public Boolean userRegisterByAdmin(String email){

        String token = UUID.randomUUID().toString();
        //confirmationTokenRepository.insert(confirmationToken);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("pedibus.polito1819@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:4200/register/"
                + token);
        emailSenderService.sendEmail(mailMessage);

        return true;
    }

    @Transactional
    public int userRegister(User newUser){
        User existingUser = userRepo.findByRegistrationNumber(newUser.getRegistrationNumber());
        if(existingUser!=null){
            return -1;
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setEnabled(false);
        List<String> roles = new LinkedList<>();
        roles.add("ROLE_USER"); //TODO: Role deciso da chi l'ha aggiunto
        newUser.setRoles(roles);
        userRepo.insert(newUser);



        return 0;
    }

    public List<Message> userGetMessages(String email){
        Optional<User> user = userRepo.findByEmail(email);
        return user.map(User::getMessages).orElse(null);
    }

    @Transactional
    public boolean userPutMessages(String email,List<Message> msgs){
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(!userOptional.isPresent()){
            return false;
        }
        User user = userOptional.get();
        //#SeAvanzaTempo: Scommentare riga sottostante per aggiungere messaggi anziché sostituirli
        //user.getMessages().addAll(msgs);
        user.setMessages(msgs);
        userRepo.save(user);
        return true;
    }

    public long userGetUnreadMessagesCount(String email){
        Optional<User> userOptional = userRepo.findByEmail(email);
        User user;
        if(!userOptional.isPresent()){
            return -1;
        }
        user = userOptional.get();
        return user.getMessages().stream()
                .filter(m -> !m.isRead())
                .count();
    }

    @Transactional
    public int userConfirmAccount(String randomUUID){
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(randomUUID);
        if(token != null) {
            Optional<User> user = userRepo.findByEmail(token.getUser().getEmail());
            if(user.isPresent()) {
                user.get().setEnabled(true);
                userRepo.save(user.get());
                return 0;
            }
        }
        return -1;
    }

    public List<User> usersGet(){
        return userRepo.findAll();
    }
    public List<String> userGetNames(){
        return userRepo.findAll().stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public int userEnableAdmin(String email, ServletRequest req,
                               String line_name,
                               Boolean enableAdmin){
        Optional<User> opt_user = userRepo.findById(email);
        if(!opt_user.isPresent()){
            return -1;
        }
        User user = opt_user.get();
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        String username = jwtTokenProvider.getUsername(token);
        if(username == null)
            return -2;

        Line line = lineRepository.findByLineName(line_name);

        if(line == null)
            return -3;

        if(enableAdmin){
            if(line.getAdmins().contains(user.getEmail())) {
                return -4;
            }
            if(user.getRoles().contains("ROLE_USER")){
                user.getRoles().remove("ROLE_USER");
                user.getRoles().add("ROLE_ADMIN");
            }
            user.getAdminLines().add(line_name);
            line.getAdmins().add(user.getEmail());
            userRepo.save(user);
            lineRepository.save(line);

        }
        else if(user.getAdminLines().contains(line_name) && line.getAdmins().contains(user.getEmail())){
            user.getAdminLines().remove(line_name);
            line.getAdmins().remove(user.getEmail());
            if(user.getAdminLines().isEmpty() && user.getRoles().contains("ROLE_ADMIN")){
                user.getRoles().remove("ROLE_ADMIN");
                user.getRoles().add("ROLE_USER");
            }
            userRepo.save(user);
            lineRepository.save(line);
        }
        return 0;
    }
}
