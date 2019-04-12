package it.polito.appinternet.pedibus;

import it.polito.appinternet.pedibus.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PedibusApplication {

    @Autowired
    private LineRepository lines;
    public static void main(String[] args) {
        SpringApplication.run(PedibusApplication.class, args);
    }

}
