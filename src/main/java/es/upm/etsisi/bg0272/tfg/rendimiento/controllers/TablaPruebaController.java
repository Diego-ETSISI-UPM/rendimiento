package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.TablaPruebaConsultaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TablaPruebaController {
    private final TablaPruebaConsultaRepository repository;

    public TablaPruebaController(TablaPruebaConsultaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tablaPruebaConsultaBDD")
    public String pruebaDB(Model model) {
        String texto = repository.obtenerTexto(); // SELECT texto FROM prueba WHERE id=2
        model.addAttribute("mensaje", texto);
        model.addAttribute("filas", repository.obtenerTodos());
        return "tablaPruebaConsultaBDD"; // muestra templates/tablaPruebaConsultaBDD.html
    }
}