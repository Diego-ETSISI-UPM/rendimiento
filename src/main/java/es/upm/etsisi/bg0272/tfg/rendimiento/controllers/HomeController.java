package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home"; // templates/home.html
    }
}

