package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowAsignaturaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowAsignaturaRepositoryTestF {
    // -------- TESTS --------
    @Test
    void funcional_listaAsignaturas() {
        var repo = new RepoAdapter(new Stub());
        assertEquals(3, repo.obtenerListaAsignaturas().size());
    }

    @Test
    void funcional_exacta_y_like() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.existeAsignaturaExacta("Matemáticas"));
        assertFalse(repo.existeAsignaturaExacta("Biología"));
        assertTrue(repo.buscarAsignaturas("").isEmpty());
        assertEquals(List.of("Matemáticas"), repo.buscarAsignaturas("mat"));
        assertTrue(repo.buscarAsignaturas("xyz").isEmpty());
    }

    @Test
    void funcional_evolucion() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.obtenerEvolucion("Física").isEmpty());
        assertEquals(2, repo.obtenerEvolucion("Matemáticas").size());
    }

    // ---------- CONTRACT ----------
    interface AsignaturaContract {
        List<String> lista();

        boolean exacta(String s);

        List<String> like(String q);

        List<Map<String, Object>> evolucion(String asig);
    }

    // ---------- STUB ----------
    static class Stub implements AsignaturaContract {
        @Override
        public List<String> lista() {
            return List.of("Matemáticas", "Física", "Química");
        }

        @Override
        public boolean exacta(String s) {
            return "Matemáticas".equalsIgnoreCase(s);
        }

        @Override
        public List<String> like(String q) {
            if (q == null || q.isBlank()) return List.of();
            if (q.toLowerCase().contains("mat")) return List.of("Matemáticas");
            return List.of();
        }

        @Override
        public List<Map<String, Object>> evolucion(String asig) {
            if (!"Matemáticas".equals(asig)) return List.of();
            return List.of(
                    Map.of("ano_academico", "2020-2021", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("ano_academico", "2021-2022", "rendimiento", BigDecimal.valueOf(85))
            );
        }
    }

    // ---------- ADAPTER ----------
    static class RepoAdapter extends ShowAsignaturaRepository {
        private final AsignaturaContract b;

        public RepoAdapter(AsignaturaContract b) {
            super(null);
            this.b = b;
        }

        @Override
        public List<String> obtenerListaAsignaturas() {
            return b.lista();
        }

        @Override
        public boolean existeAsignaturaExacta(String s) {
            return b.exacta(s);
        }

        @Override
        public List<String> buscarAsignaturas(String q) {
            return b.like(q);
        }

        @Override
        public List<Map<String, Object>> obtenerEvolucion(String a) {
            return b.evolucion(a);
        }
    }
}