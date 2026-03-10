package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ShowAsignaturaController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowAsignaturaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowAsignaturaControllerTestF {
    // ---------- TESTS ----------
    @Test
    void get_showAsignatura() {
        var c = new ShowAsignaturaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.showAsignatura(m);
        assertEquals("showAsignatura", vista);
    }

    @Test
    void post_asignaturaExacta() {
        var c = new ShowAsignaturaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.mostrarEvolucion("Matemáticas", m);
        assertEquals("showAsignatura", vista);
        assertEquals("Matemáticas", m.getAttribute("seleccionada"));
        assertNotNull(m.getAttribute("evolucion"));
        assertNotNull(m.getAttribute("labels"));
        assertNotNull(m.getAttribute("rendimientos"));
    }

    @Test
    void post_variasCoincidencias() {
        var c = new ShowAsignaturaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.mostrarEvolucion("fi", m);
        assertEquals("showAsignatura", vista);
        assertTrue(m.containsAttribute("coincidencias"));
        assertTrue(m.containsAttribute("mensaje"));
    }

    @Test
    void post_sinCoincidencias() {
        var c = new ShowAsignaturaController(new RepoAdapter(new Stub()));
        Model m = new ExtendedModelMap();
        String vista = c.mostrarEvolucion("XYZ", m);
        assertEquals("showAsignatura", vista);
        assertEquals("XYZ", m.getAttribute("seleccionada"));
        assertTrue(m.containsAttribute("mensaje"));
    }

    @Test
    void api_buscar() {
        var c = new ShowAsignaturaController(new RepoAdapter(new Stub()));
        var lista = c.buscarAsignaturas("mat");
        assertEquals(List.of("Matemáticas"), lista);
    }

    // ---------- CONTRACT ----------
    interface AsigContract {
        boolean exacta(String nombre);

        List<String> buscar(String q);

        List<Map<String, Object>> evolucion(String nombre);
    }

    // ---------- STUB ----------
    static class Stub implements AsigContract {
        @Override
        public boolean exacta(String n) {
            return "Matemáticas".equalsIgnoreCase(n);
        }

        @Override
        public List<String> buscar(String q) {
            if (q == null || q.isBlank()) return List.of();
            if (q.equalsIgnoreCase("mat")) return List.of("Matemáticas");
            if (q.equalsIgnoreCase("fi")) return List.of("Física", "Filosofía");
            return List.of();
        }

        @Override
        public List<Map<String, Object>> evolucion(String n) {
            if (!"Matemáticas".equalsIgnoreCase(n)) return List.of();
            return List.of(
                    Map.of("ano_academico", "2020-2021", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("ano_academico", "2021-2022", "rendimiento", BigDecimal.valueOf(85))
            );
        }
    }

    // ---------- ADAPTER ----------
    static class RepoAdapter extends ShowAsignaturaRepository {
        private final AsigContract b;

        public RepoAdapter(AsigContract b) {
            super(null);
            this.b = b;
        }

        @Override
        public boolean existeAsignaturaExacta(String n) {
            return b.exacta(n);
        }

        @Override
        public List<String> buscarAsignaturas(String q) {
            return b.buscar(q);
        }

        @Override
        public List<Map<String, Object>> obtenerEvolucion(String a) {
            return b.evolucion(a);
        }
    }
}