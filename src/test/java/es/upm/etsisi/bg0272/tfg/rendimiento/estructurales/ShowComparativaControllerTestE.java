package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ShowComparativaController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowComparativaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShowComparativaControllerTestE {
    ShowComparativaController controller;

    @BeforeEach
    void setUp() {
        controller = new ShowComparativaController(new RepoAdapter(new RepoStub()));
    }

    // -------- TESTS --------
    @Test
    void get_devuelveVista() {
        Model model = new ExtendedModelMap();
        String vista = controller.comparativaGet(model);
        assertEquals("showComparativa", vista);
    }

    @Test
    void post_sinAsignaturas() {
        Model model = new ExtendedModelMap();
        var vista = controller.comparativaPost(null, model);
        assertEquals("showComparativa", vista);
        assertTrue(model.containsAttribute("mensaje"));
    }

    @Test
    void post_conDuplicadosYEspacios() {
        Model model = new ExtendedModelMap();
        var vista = controller.comparativaPost(List.of(" Matemáticas ", "Matemáticas", "Física  "), model);
        assertEquals("showComparativa", vista);
        List<String> seleccionadas = (List<String>) model.getAttribute("seleccionadas");
        assertEquals(List.of("Matemáticas", "Física"), seleccionadas);
        assertNotNull(model.getAttribute("labels"));
        assertNotNull(model.getAttribute("valores"));
        assertNotNull(model.getAttribute("resultados"));
    }

    // -------- CONTRACT --------
    interface ComparativaRepoContract {
        List<Map<String, Object>> obtenerRendimientoComparativa(List<String> asignaturas);
    }

    // -------- STUB --------
    static class RepoStub implements ComparativaRepoContract {
        @Override
        public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> asignaturas) {
            return List.of(
                    Map.of("asignatura", "Matemáticas", "rendimiento", BigDecimal.valueOf(80)),
                    Map.of("asignatura", "Física", "rendimiento", BigDecimal.valueOf(70))
            );
        }
    }

    // -------- ADAPTER --------
    static class RepoAdapter extends ShowComparativaRepository {
        private final ComparativaRepoContract backing;

        public RepoAdapter(ComparativaRepoContract backing) {
            super(null);
            this.backing = backing;
        }

        @Override
        public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> a) {
            return backing.obtenerRendimientoComparativa(a);
        }
    }
}