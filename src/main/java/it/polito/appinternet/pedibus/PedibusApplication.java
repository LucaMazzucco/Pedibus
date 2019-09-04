package it.polito.appinternet.pedibus;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import it.polito.appinternet.pedibus.model.Message;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.UserRepository;
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
        List<Message> messages = new LinkedList<>();
        messages.add(new Message(Instant.now().getEpochSecond(), false, "Questo è il primo messaggio che ti mando"));
        messages.add(new Message(Instant.now().getEpochSecond(), false, "Questo è il secondo messaggio che ti mando"));
        u.setMessages(messages);
        List<String> roles = new LinkedList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        u.setRoles(roles);
        userRepo.insert(u);
    }
}
