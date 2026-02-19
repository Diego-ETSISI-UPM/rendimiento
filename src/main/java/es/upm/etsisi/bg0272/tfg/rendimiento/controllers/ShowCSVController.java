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
            return "showCSV";
        }

        var lineas = repository.obtenerLineas();

        if (lineas.isEmpty()) {
            model.addAttribute("estado", "vacia");
            return "showCSV";
        }

        model.addAttribute("estado", "ok");
        model.addAttribute("cabeceras", repository.obtenerCabeceras());
        model.addAttribute("lineas", lineas);

        return "showCSV";
    }
}