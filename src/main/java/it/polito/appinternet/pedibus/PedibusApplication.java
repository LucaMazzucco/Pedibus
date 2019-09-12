package it.polito.appinternet.pedibus;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import it.polito.appinternet.pedibus.model.Message;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.model.Child;
import it.polito.appinternet.pedibus.repository.UserRepository;
import it.polito.appinternet.pedibus.repository.ChildRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@EnableEmailTools
public class PedibusApplication {


    @Autowired
    UserRepository userRepo;
    @Autowired
    ChildRepository childRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(PedibusApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    Logger logger = LoggerFactory.getLogger(PedibusApplication.class);
    @PostConstruct
    public void init() {
        mongoTemplate.getDb().drop();
        //Add the system-admin
        User u = new User("admin@admin.com", passwordEncoder.encode("admin"), true);
        User p = new User("parent@parent.com", passwordEncoder.encode("parent"), true);
        User c = new User("conductor@conductor.com", passwordEncoder.encode("conductor"), true);
        Child c1 = new Child("Ginevra", "Rossi", "RSSGNV04R20E290Z", u.getId());
        Child c2 = new Child("Francesco", "Neri", "NRIFRN05R20E291Z", p.getId());
        Child c3 = new Child("Michele", "Verdi", "VRDMCH07R20E290Z", c.getId());

        childRepository.save(c1);
        childRepository.save(c2);
        childRepository.save(c3);

        List<Message> messages = new LinkedList<>();
        messages.add(new Message(Instant.now().getEpochSecond(), false, "Questo è il primo messaggio che ti mando"));
        messages.add(new Message(Instant.now().getEpochSecond(), false, "Questo è il secondo messaggio che ti mando"));
        u.setMessages(messages);
        p.setMessages(messages);
        c.setMessages(messages);
        List<String> chRegs = new LinkedList<>();
        List<String> roles = new LinkedList<>();
        List<String> rolesp = new LinkedList<>();
        List<String> rolesc = new LinkedList<>();

        chRegs.add(c1.getRegistrationNumber());
        chRegs.add(c2.getRegistrationNumber());
        chRegs.add(c3.getRegistrationNumber());

        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        rolesp.add("ROLE_USER");
        rolesc.add("ROLE_CONDUCTOR");

        u.setRoles(roles);
        p.setRoles(rolesp);
        c.setRoles(rolesc);

        u.setChildren(chRegs);
        p.setChildren(chRegs);
        c.setChildren(chRegs);

        userRepo.insert(u);
        userRepo.insert(p);
        userRepo.insert(c);
    }
}
