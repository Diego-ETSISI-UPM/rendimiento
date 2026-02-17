package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.PruebaConsultaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TablaPruebaController {
    private final PruebaConsultaRepository repository;

    public TablaPruebaController(PruebaConsultaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/PruebaConsultaBDD")
    public String pruebaDB(Model model) {
        String texto = repository.obtenerTexto(); // SELECT texto FROM prueba WHERE id=2
        model.addAttribute("mensaje", texto);
        model.addAttribute("filas", repository.obtenerTodos());
        return "PruebaConsultaBDD"; // muestra templates/PruebaConsultaBDD.html
    }
}