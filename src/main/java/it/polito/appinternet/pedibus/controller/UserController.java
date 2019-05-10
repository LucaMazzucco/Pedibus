package it.polito.appinternet.pedibus.controller;

import it.polito.appinternet.pedibus.model.EmailSenderService;
import it.polito.appinternet.pedibus.model.User;
import it.polito.appinternet.pedibus.repository.ConfirmationTokenRepository;
import it.polito.appinternet.pedibus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public ModelAndView displayRegistration(ModelAndView modelAndView, User user)
    {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }
}
