package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ShowRankingController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowRankingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowRankingControllerTestF {
    // -------- TESTS --------
    @Test
    void get_showRanking() {
        var c = new ShowRankingController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.getRanking(m);
        assertEquals("showRanking", vista);
        assertEquals(List.of("Plan A", "Plan B"), m.getAttribute("planes"));
    }

    @Test
    void post_sinAsignaturas() {
        var c = new ShowRankingController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.postRanking("Plan A", null, m);
        assertEquals("showRanking", vista);
        assertTrue(m.containsAttribute("mensaje"));
        assertEquals("Plan A", m.getAttribute("planSeleccionado"));
    }

    @Test
    void post_conAsignaturas() {
        var c = new ShowRankingController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.postRanking("Plan A", List.of("Matemáticas"), m);
        assertEquals("showRanking", vista);
        assertNotNull(m.getAttribute("resultados"));
        assertNotNull(m.getAttribute("labels"));
        assertNotNull(m.getAttribute("valores"));
    }

    @Test
    void api_asignaturasPlan() {
        var c = new ShowRankingController(new RepoAdapter(new Stub()));
        var lista = c.buscarPorPlan("Plan A", "m");
        assertEquals(List.of("Matemáticas", "Física"), lista);
    }

    // ---------- CONTRACT ----------
    interface Contract {
        List<String> planes();

        List<String> asignPlan(String plan, String q);

        List<Map<String, Object>> ranking(String plan, List<String> asigs);
    }

    // ---------- STUB ----------
    static class Stub implements Contract {
        @Override
        public List<String> planes() {
            return List.of("Plan A", "Plan B");
        }

        @Override
        public List<String> asignPlan(String plan, String q) {
            if (q == null || q.isBlank()) return List.of();
            return List.of("Matemáticas", "Física");
        }

        @Override
        public List<Map<String, Object>> ranking(String p, List<String> a) {
            if (a == null || a.isEmpty()) return List.of();
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("asignatura", "Física", "rendimiento", BigDecimal.valueOf(60))
            );
        }
    }

    // ---------- ADAPTER ----------
    static class RepoAdapter extends ShowRankingRepository {
        private final Contract b;

        public RepoAdapter(Contract b) {
            super(null);
            this.b = b;
        }

        @Override
        public List<String> obtenerPlanes() {
            return b.planes();
        }

        @Override
        public List<String> buscarAsignaturasDePlan(String p, String q) {
            return b.asignPlan(p, q);
        }

        @Override
        public List<Map<String, Object>> rankingUltimoAnio(String p, List<String> a) {
            return b.ranking(p, a);
        }
    }
}