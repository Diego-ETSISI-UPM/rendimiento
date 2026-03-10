package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowRankingRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowRankingRepositoryTestF {
    // ---- TEST ----
    @Test
    void ranking_valoresEspeciales() {
        var repo = new RepoAdapter(new RepoStub());
        assertEquals(List.of("Plan A", "Plan B"), repo.obtenerPlanes());
        assertTrue(repo.buscarAsignaturasDePlan("Plan A", "").isEmpty());
        assertEquals(2, repo.buscarAsignaturasDePlan("Plan A", "m").size());
        assertTrue(repo.rankingUltimoAnio("Plan A", null).isEmpty());
        assertTrue(repo.rankingUltimoAnio("Plan A", List.of()).isEmpty());
        assertEquals(2, repo.rankingUltimoAnio("Plan A", List.of("mat")).size());
    }

    // ---------- CONTRACT ----------
    interface RankingContract {
        List<String> obtenerPlanes();

        List<String> buscarAsignaturasDePlan(String plan, String q);

        List<Map<String, Object>> rankingUltimoAnio(String plan, List<String> asign);
    }

    // ---------- STUB ----------
    static class RepoStub implements RankingContract {
        @Override
        public List<String> obtenerPlanes() {
            return List.of("Plan A", "Plan B");
        }

        @Override
        public List<String> buscarAsignaturasDePlan(String plan, String q) {
            if (q == null || q.isBlank()) return List.of();
            return List.of("Matemáticas", "Física");
        }

        @Override
        public List<Map<String, Object>> rankingUltimoAnio(String plan, List<String> asign) {
            if (asign == null || asign.isEmpty()) return List.of();
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("asignatura", "Física", "rendimiento", BigDecimal.valueOf(60))
            );
        }
    }

    // ---------- ADAPTER ----------
    static class RepoAdapter extends ShowRankingRepository {
        private final RankingContract backing;

        public RepoAdapter(RankingContract backing) {
            super(null);
            this.backing = backing;
        }

        @Override
        public List<String> obtenerPlanes() {
            return backing.obtenerPlanes();
        }

        @Override
        public List<String> buscarAsignaturasDePlan(String p, String q) {
            return backing.buscarAsignaturasDePlan(p, q);
        }

        @Override
        public List<Map<String, Object>> rankingUltimoAnio(String p, List<String> a) {
            return backing.rankingUltimoAnio(p, a);
        }
    }
}