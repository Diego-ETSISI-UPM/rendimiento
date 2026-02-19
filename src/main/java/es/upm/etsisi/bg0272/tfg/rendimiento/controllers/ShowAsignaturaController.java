package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowAsignaturaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ShowAsignaturaController {

    private final ShowAsignaturaRepository repo;

    public ShowAsignaturaController(ShowAsignaturaRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/showAsignatura")
    public String showAsignatura(Model model) {
        // ya no cargamos miles de opciones; la búsqueda será por AJAX
        return "showAsignatura";
    }

    @PostMapping("/showAsignatura")
    public String mostrarEvolucion(@RequestParam("asignatura") String asignatura, Model model) {
        var datos = repo.obtenerEvolucion(asignatura);

        // Arrays para Chart.js (si los usas)
        List<String> labels = new ArrayList<>();
        List<BigDecimal> rendimientos = new ArrayList<>();
        for (var fila : datos) {
            labels.add((String) fila.get("ano_academico"));
            rendimientos.add((BigDecimal) fila.get("rendimiento"));
        }

        model.addAttribute("seleccionada", asignatura);
        model.addAttribute("evolucion", datos);
        model.addAttribute("labels", labels);
        model.addAttribute("rendimientos", rendimientos);

        return "showAsignatura";
    }

    // ---- REST (autocompletado) ----
    @GetMapping(value = "/api/asignaturas/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> buscarAsignaturas(@RequestParam("q") String q) {
        // Delegamos en el repositorio (LIKE %q%, LIMIT 20)
        return repo.buscarAsignaturas(q);
    }
}
