package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ChildController {
    @Autowired
    ChildService childService;



}
