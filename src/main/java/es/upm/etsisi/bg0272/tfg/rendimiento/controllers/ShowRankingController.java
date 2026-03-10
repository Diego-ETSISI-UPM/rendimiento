package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowRankingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ShowRankingController {

    private final ShowRankingRepository repo;

    public ShowRankingController(ShowRankingRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/showRanking")
    public String getRanking(Model model) {
        model.addAttribute("planes", repo.obtenerPlanes());
        return "showRanking";
    }

    @PostMapping("/showRanking")
    public String postRanking(
            @RequestParam("plan") String plan,
            @RequestParam(name = "asignaturas", required = false) List<String> asignaturas,
            Model model) {

        model.addAttribute("planes", repo.obtenerPlanes());
        System.out.println("POST /showRanking → plan=" + plan + " asignaturas=" + asignaturas);
        model.addAttribute("planSeleccionado", plan);
        if (asignaturas == null || asignaturas.isEmpty()) {
            model.addAttribute("mensaje", "Selecciona al menos una asignatura.");
            return "showRanking";

        }

        var datos = repo.rankingUltimoAnio(plan, asignaturas);

        model.addAttribute("resultados", datos);

        // arrays para Chart.js
        List<String> labels = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        for (var fila : datos) {
            labels.add((String) fila.get("asignatura"));
            valores.add(((Number) fila.get("rendimiento")).doubleValue());
        }

        model.addAttribute("labels", labels);
        model.addAttribute("valores", valores);

        return "showRanking";
    }

    // autocompletado
    @GetMapping("/api/asignaturas/plan")
    @ResponseBody
    public List<String> buscarPorPlan(
            @RequestParam("plan") String plan,
            @RequestParam("q") String q) {
        return repo.buscarAsignaturasDePlan(plan, q);
    }
}
