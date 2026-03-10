package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowComparativaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Controller
public class ShowComparativaController {

    private final ShowComparativaRepository repo;

    public ShowComparativaController(ShowComparativaRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/showComparativa")
    public String comparativaGet(Model model) {
        return "showComparativa";
    }

    @PostMapping("/showComparativa")
    public String comparativaPost(@RequestParam(name = "asignaturas", required = false) List<String> seleccion, Model model) {
        if (seleccion == null || seleccion.isEmpty()) {
            model.addAttribute("mensaje", "Añade al menos una asignatura para comparar.");
            return "showComparativa";
        }

        // normalizar, sin duplicados y recortando espacios
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

        // preparar arrays para la gráfica de barras horizontales
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
