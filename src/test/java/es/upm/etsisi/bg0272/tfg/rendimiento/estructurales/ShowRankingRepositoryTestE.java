package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowRankingRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowRankingRepositoryTestE {
    // ---- TESTS ----
    @Test
    void obtenerPlanes() {
        var repo = new RepoAdapter(new Stub());
        assertEquals(List.of("Plan A", "Plan B"), repo.obtenerPlanes());
    }

    @Test
    void asignDePlan_valoresEspeciales() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.buscarAsignaturasDePlan("Plan A", "").isEmpty());
        assertEquals(2, repo.buscarAsignaturasDePlan("Plan A", "m").size());
    }

    @Test
    void ranking_valoresEspeciales() {
        var repo = new RepoAdapter(new Stub());
        assertTrue(repo.rankingUltimoAnio("Plan A", null).isEmpty());
        assertTrue(repo.rankingUltimoAnio("Plan A", List.of()).isEmpty());
        assertEquals(2, repo.rankingUltimoAnio("Plan A", List.of("m")).size());
    }

    // ---- CONTRACT ----
    interface RankingContract {
        List<String> planes();

        List<String> asignDePlan(String plan, String q);

        List<Map<String, Object>> ranking(String plan, List<String> asign);
    }

    // ---- STUB ----
    static class Stub implements RankingContract {
        @Override
        public List<String> planes() {
            return List.of("Plan A", "Plan B");
        }

        @Override
        public List<String> asignDePlan(String p, String q) {
            if (q == null || q.isBlank()) return List.of();
            return List.of("Matemáticas", "Física");
        }

        @Override
        public List<Map<String, Object>> ranking(String plan, List<String> a) {
            if (a == null || a.isEmpty()) return List.of();
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("asignatura", "Física", "rendimiento", BigDecimal.valueOf(60))
            );
        }
    }

    // ---- ADAPTER ----
    static class RepoAdapter extends ShowRankingRepository {
        private final RankingContract b;

        public RepoAdapter(RankingContract b) {
            super(null);
            this.b = b;
        }

        @Override
        public List<String> obtenerPlanes() {
            return b.planes();
        }

        @Override
        public List<String> buscarAsignaturasDePlan(String p, String q) {
            return b.asignDePlan(p, q);
        }

        @Override
        public List<Map<String, Object>> rankingUltimoAnio(String p, List<String> a) {
            return b.ranking(p, a);
        }
    }
}