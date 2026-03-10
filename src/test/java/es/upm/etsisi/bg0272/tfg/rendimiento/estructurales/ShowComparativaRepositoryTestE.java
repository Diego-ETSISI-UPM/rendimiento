package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowComparativaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowComparativaRepositoryTestE {
    // ---- TESTS ----
    @Test
    void comparativa_valoresEspeciales() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.obtenerRendimientoComparativa(null).isEmpty());
        assertTrue(repo.obtenerRendimientoComparativa(List.of()).isEmpty());
        var res = repo.obtenerRendimientoComparativa(List.of("Algo"));
        assertEquals(2, res.size());
    }

    // ---- CONTRACT ----
    interface ComparContract {
        List<Map<String, Object>> comp(List<String> asignaturas);
    }

    // ---- STUB ----
    static class Stub implements ComparContract {
        @Override
        public List<Map<String, Object>> comp(List<String> a) {
            if (a == null || a.isEmpty()) return List.of();
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("asignatura", "Física", "rendimiento", BigDecimal.valueOf(70))
            );
        }
    }

    // ---- ADAPTER ----
    static class RepoAdapter extends ShowComparativaRepository {
        private final ComparContract b;

        public RepoAdapter(ComparContract b) {
            super(null);
            this.b = b;
        }

        @Override
        public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> a) {
            return b.comp(a);
        }
    }
}