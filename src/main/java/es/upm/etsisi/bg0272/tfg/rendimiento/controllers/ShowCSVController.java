package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowCSVRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShowCSVController {

    private final ShowCSVRepository repository;

    public ShowCSVController(ShowCSVRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/showCSV")
    public String mostrarCSV(Model model) {

        if (!repository.tablaExiste()) {
            model.addAttribute("estado", "no_tabla");
            return "ShowCSV";
        }

        var lineas = repository.obtenerLineas();

        if (lineas.isEmpty()) {
            model.addAttribute("estado", "vacia");
            return "ShowCSV";
        }

        model.addAttribute("estado", "ok");
        model.addAttribute("lineas", lineas);

        return "ShowCSV";
    }
}