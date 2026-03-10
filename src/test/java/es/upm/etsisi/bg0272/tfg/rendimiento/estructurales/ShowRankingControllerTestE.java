package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ShowRankingController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowRankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowRankingControllerTestE {
    ShowRankingController controller;
    RepoStub repo;

    @BeforeEach
    void setUp() {
        repo = new RepoStub();
        controller = new ShowRankingController(new RepoAdapter(repo));
    }

    // ---- TESTS ----
    @Test
    void getRanking_devuelveVista() {
        Model model = new ExtendedModelMap();
        String vista = controller.getRanking(model);
        assertEquals("showRanking", vista);
        assertEquals(repo.planes, model.getAttribute("planes"));
    }

    @Test
    void postRanking_asignaturasNull() {
        Model model = new ExtendedModelMap();
        var vista = controller.postRanking("Plan A", null, model);
        assertEquals("showRanking", vista);
        assertTrue(model.containsAttribute("mensaje"));
    }

    @Test
    void postRanking_asignaturasVacio() {
        Model model = new ExtendedModelMap();
        var vista = controller.postRanking("Plan A", List.of(), model);
        assertEquals("showRanking", vista);
        assertTrue(model.containsAttribute("mensaje"));
    }

    @Test
    void postRanking_ok() {
        Model model = new ExtendedModelMap();
        var vista = controller.postRanking("Plan A", List.of("Matemáticas"), model);
        assertEquals("showRanking", vista);
        assertNotNull(model.getAttribute("resultados"));
        assertNotNull(model.getAttribute("labels"));
        assertNotNull(model.getAttribute("valores"));
    }

    @Test
    void api_buscarPorPlan() {
        var lista = controller.buscarPorPlan("Plan A", "Ma");
        assertEquals(List.of("Matemáticas", "Física"), lista);
    }

    // ---- CONTRACT ----
    interface RankingRepoContract {
        List<String> obtenerPlanes();

        List<Map<String, Object>> rankingUltimoAnio(String plan, List<String> asignaturas);

        List<String> buscarAsignaturasDePlan(String plan, String q);
    }

    // ---- STUB ----
    static class RepoStub implements RankingRepoContract {
        List<String> planes = List.of("Plan A", "Plan B");

        @Override
        public List<String> obtenerPlanes() {
            return planes;
        }

        @Override
        public List<Map<String, Object>> rankingUltimoAnio(String plan, List<String> asignaturas) {
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", 70),
                    Map.of("asignatura", "Física", "rendimiento", 60)
            );
        }

        @Override
        public List<String> buscarAsignaturasDePlan(String plan, String q) {
            return List.of("Matemáticas", "Física");
        }
    }

    // ---- ADAPTER ----
    static class RepoAdapter extends ShowRankingRepository {
        private final RankingRepoContract backing;

        public RepoAdapter(RankingRepoContract backing) {
            super(null);
            this.backing = backing;
        }

        @Override
        public List<String> obtenerPlanes() {
            return backing.obtenerPlanes();
        }

        @Override
        public List<Map<String, Object>> rankingUltimoAnio(String p, List<String> a) {
            return backing.rankingUltimoAnio(p, a);
        }

        @Override
        public List<String> buscarAsignaturasDePlan(String p, String q) {
            return backing.buscarAsignaturasDePlan(p, q);
        }
    }
}