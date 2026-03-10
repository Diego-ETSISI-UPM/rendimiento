package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ShowComparativaController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowComparativaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowComparativaControllerTestF {
    // -------- TESTS --------
    @Test
    void getComparativa() {
        var c = new ShowComparativaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        assertEquals("showComparativa", c.comparativaGet(m));
    }

    @Test
    void post_sinAsignaturas() {
        var c = new ShowComparativaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.comparativaPost(null, m);
        assertEquals("showComparativa", vista);
        assertTrue(m.containsAttribute("mensaje"));
    }

    @Test
    void post_conAsignaturas_normalizadas() {
        var c = new ShowComparativaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.comparativaPost(List.of(" A ", "A", "B "), m);
        assertEquals("showComparativa", vista);
        assertEquals(List.of("A", "B"), m.getAttribute("seleccionadas"));
        assertNotNull(m.getAttribute("resultados"));
        assertNotNull(m.getAttribute("labels"));
        assertNotNull(m.getAttribute("valores"));
    }

    // ---------- CONTRACT ----------
    interface Contract {
        List<Map<String, Object>> comp(List<String> asign);
    }

    // ---------- STUB ----------
    static class Stub implements Contract {
        @Override
        public List<Map<String, Object>> comp(List<String> a) {
            if (a == null || a.isEmpty()) return List.of();
            return List.of(
                    Map.of("asignatura", "A", "rendimiento", BigDecimal.valueOf(50)),
                    Map.of("asignatura", "B", "rendimiento", BigDecimal.valueOf(60))
            );
        }
    }

    // ---------- ADAPTER ----------
    static class RepoAdapter extends ShowComparativaRepository {
        private final Contract b;

        public RepoAdapter(Contract b) {
            super(null);
            this.b = b;
        }

        @Override
        public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> a) {
            return b.comp(a);
        }
    }
}