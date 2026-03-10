package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowAsignaturaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowAsignaturaRepositoryTestE {
    // -------- TESTS --------
    @Test
    void listaAsignaturas() {
        var repo = new RepoAdapter(new Stub());
        var lista = repo.obtenerListaAsignaturas();
        assertEquals(2, lista.size());
    }

    @Test
    void exacta_y_like() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.existeAsignaturaExacta("Matemáticas"));
        assertFalse(repo.existeAsignaturaExacta("X"));
        assertTrue(repo.buscarAsignaturas("").isEmpty());
        assertEquals(List.of("Matemáticas"), repo.buscarAsignaturas("mat"));
    }

    @Test
    void evolucion() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.obtenerEvolucion("X").isEmpty());
        assertEquals(2, repo.obtenerEvolucion("Matemáticas").size());
    }

    // -------- CONTRACT --------
    interface AsignaturaContract {
        List<String> queryLista();

        boolean exists(String exact);

        List<String> queryLike(String q);

        List<Map<String, Object>> queryEvolucion(String asign);
    }

    // -------- STUB --------
    static class Stub implements AsignaturaContract {
        @Override
        public List<String> queryLista() {
            return List.of("Matemáticas", "Física");
        }

        @Override
        public boolean exists(String e) {
            return "Matemáticas".equals(e);
        }

        @Override
        public List<String> queryLike(String q) {
            if (q == null || q.isBlank()) return List.of();
            if (q.equalsIgnoreCase("mat")) return List.of("Matemáticas");
            return List.of();
        }

        @Override
        public List<Map<String, Object>> queryEvolucion(String asign) {
            if (!"Matemáticas".equals(asign)) return List.of();
            return List.of(
                    Map.of("ano_academico", "2021-2022", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("ano_academico", "2022-2023", "rendimiento", BigDecimal.valueOf(82))
            );
        }
    }

    // -------- ADAPTER --------
    static class RepoAdapter extends ShowAsignaturaRepository {
        private final AsignaturaContract b;

        public RepoAdapter(AsignaturaContract b) {
            super(null);
            this.b = b;
        }

        @Override
        public List<String> obtenerListaAsignaturas() {
            return b.queryLista();
        }

        @Override
        public boolean existeAsignaturaExacta(String n) {
            return b.exists(n);
        }

        @Override
        public List<String> buscarAsignaturas(String q) {
            return b.queryLike(q);
        }

        @Override
        public List<Map<String, Object>> obtenerEvolucion(String a) {
            return b.queryEvolucion(a);
        }
    }
}
