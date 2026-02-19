package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowAsignaturaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShowAsignaturaController {

    private final ShowAsignaturaRepository repo;

    public ShowAsignaturaController(ShowAsignaturaRepository repo) { this.repo = repo; }

    @GetMapping("/showAsignatura")
    public String ShowAsignatura(Model model) {
        model.addAttribute("asignaturas", repo.obtenerListaAsignaturas());
        return "showAsignatura";
    }

    @PostMapping("/showAsignatura")
    public String mostrarEvolucion(@RequestParam("asignatura") String asignatura, Model model) {

        var datos = repo.obtenerEvolucion(asignatura);

        model.addAttribute("asignaturas", repo.obtenerListaAsignaturas());
        model.addAttribute("seleccionada", asignatura);
        model.addAttribute("evolucion", datos);

        return "showAsignatura";
    }
}

