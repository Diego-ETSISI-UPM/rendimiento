package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    // muestra la página con valores por defecto
    @GetMapping
    public String show(Model model, @RequestParam(value = "plan", required = false) String plan) {
        // Lista de “planes” simulados (luego vendrán de BD)
        List<String> planes = List.of("61IW - Ingeniería del Software",
                "61CI - Ingeniería de Computadores",
                "61CD - Ciencia de Datos", "TFG");
        model.addAttribute("planes", planes);
        model.addAttribute("planSeleccionado", plan);
        // Resultados simulados si ya hay selección
        if (plan != null && !plan.isBlank()) {
            model.addAttribute("kpis", new Kpis(82.4, 88.1, 4120, 3280));
            model.addAttribute("asignaturas", List.of(
                    new AsignaturaDto("Fundamentos de Programación", 69.18),
                    new AsignaturaDto("Algorítmica y Complejidad", 87.17),
                    new AsignaturaDto("Álgebra", 71.05)
            ));
        }
        return "dashboard";
    }
    // enviar por POST:
    @PostMapping
    public String search(@RequestParam("plan") String plan, Model model) {
        return show(model, plan);
    }
    // datos simples para la vista
    public record Kpis(double rendimientoMedio, double eficienciaMedia, int matriculados, int aprobados) {}
    public record AsignaturaDto(String nombre, double rendimiento) {}
}