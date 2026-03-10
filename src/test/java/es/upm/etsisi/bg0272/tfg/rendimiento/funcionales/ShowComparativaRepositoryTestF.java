package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowComparativaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowComparativaRepositoryTestF {
    // ---- TEST ----
    @Test
    void comparativa_valoresEspeciales() {
        var repo = new RepoAdapter(new RepoStub());
        assertTrue(repo.obtenerRendimientoComparativa(null).isEmpty());
        assertTrue(repo.obtenerRendimientoComparativa(List.of()).isEmpty());
        var res = repo.obtenerRendimientoComparativa(List.of("Matemáticas"));
        assertEquals(2, res.size());
    }

    // ---------- CONTRACT ----------
    interface ComparativaContract {
        List<Map<String, Object>> obtenerRendimientoComparativa(List<String> asignaturas);
    }

    // ---------- STUB ----------
    static class RepoStub implements ComparativaContract {
        @Override
        public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> asign) {
            if (asign == null || asign.isEmpty()) return List.of();
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("asignatura", "Física", "rendimiento", BigDecimal.valueOf(70))
            );
        }
    }

    // ---------- ADAPTER ----------
    static class RepoAdapter extends ShowComparativaRepository {
        private final ComparativaContract backing;

        public RepoAdapter(ComparativaContract backing) {
            super(null);
            this.backing = backing;
        }

        @Override
        public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> a) {
            return backing.obtenerRendimientoComparativa(a);
        }
    }
}
