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
    public String mostrarEvolucion(@RequestParam("asignatura") String input, Model model) {

        String consultaUsuario = input == null ? "" : input.trim();

        if (repo.existeAsignaturaExacta(consultaUsuario)) {
            return renderEvolucion(consultaUsuario, model);
        }

        var coincidencias = repo.buscarAsignaturas(consultaUsuario);

        if (coincidencias.isEmpty()) {
            model.addAttribute("mensaje", "No se encontraron asignaturas que contengan: \"" + consultaUsuario + "\"");
            // Devolvemos la página vacía pero con mensaje y el valor tecleado
            model.addAttribute("seleccionada", consultaUsuario);
            return "showAsignatura";
        }

        if (coincidencias.size() == 1) {
            String asignatura = coincidencias.get(0);
            return renderEvolucion(asignatura, model);
        }

        model.addAttribute("coincidencias", coincidencias);
        model.addAttribute("seleccionada", consultaUsuario);
        model.addAttribute("mensaje", "Hay varias asignaturas que coinciden. Elige una de la lista.");
        return "showAsignatura";
    }

    private String renderEvolucion(String asignatura, Model model) {
        var datos = repo.obtenerEvolucion(asignatura);

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

    @GetMapping(value = "/api/asignaturas/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> buscarAsignaturas(@RequestParam("q") String q) {
        return repo.buscarAsignaturas(q);
    }
}
