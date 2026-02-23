package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowComparativaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class ShowComparativaController {

    private final ShowComparativaRepository repo;

    public ShowComparativaController(ShowComparativaRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/showComparativa")
    public String comparativaGet(Model model) {
        // La selección se construye en cliente (chips) y no cargamos nada inicial
        return "showComparativa";
    }

    /**
     * Recibe múltiples asignaturas (inputs hidden con name="asignaturas")
     */
    @PostMapping("/showComparativa")
    public String comparativaPost(@RequestParam(name = "asignaturas", required = false) List<String> seleccion, Model model) {
        if (seleccion == null || seleccion.isEmpty()) {
            model.addAttribute("mensaje", "Añade al menos una asignatura para comparar.");
            return "showComparativa";
        }

        // Normalizar: sin duplicados y recortando espacios
        List<String> asignaturas = new ArrayList<>();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String s : seleccion) {
            if (s != null) {
                String v = s.trim();
                if (!v.isEmpty()) set.add(v);
            }
        }
        asignaturas.addAll(set);

        var datos = repo.obtenerRendimientoComparativa(asignaturas);

        // Preparar arrays para la gráfica (barras horizontales)
        List<String> labels = new ArrayList<>();
        List<BigDecimal> valores = new ArrayList<>();
        for (var fila : datos) {
            labels.add((String) fila.get("asignatura"));
            valores.add((BigDecimal) fila.get("rendimiento"));
        }

        model.addAttribute("seleccionadas", asignaturas);
        model.addAttribute("resultados", datos);
        model.addAttribute("labels", labels);
        model.addAttribute("valores", valores);

        return "showComparativa";
    }
}
