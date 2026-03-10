package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ShowAsignaturaController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ShowAsignaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas ESTRUCTURALES (sin Spring, sin mocks) del controlador ShowAsignaturaController.
class ShowAsignaturaControllerTestE {
    private ShowAsignaturaController controller;
    private RepoStub repoStub;

    @BeforeEach
    void setUp() {
        repoStub = new RepoStub();
        // Poblar datos base
        repoStub.exactas.add("Matemáticas");
        repoStub.porSubcadena.put("MAT", List.of("Matemáticas I", "Matemáticas II"));
        repoStub.porSubcadena.put("XYZ", List.of()); // sin coincidencias
        repoStub.porSubcadena.put("Progra", List.of("Programación"));
        // Evolución para exacta
        repoStub.evoluciones.put("Matemáticas", List.of(fila("2020-2021", 100, 80, 80.00), fila("2021-2022", 120, 100, 83.33)));
        // Evolución para única coincidencia
        repoStub.evoluciones.put("Programación", List.of(fila("2022-2023", 90, 60, 66.67)));
        controller = new ShowAsignaturaController(new RepoAdapter(repoStub));
    }

    private Map<String, Object> fila(String anio, int matric, int aprob, double rend) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ano_academico", anio);
        m.put("matriculados", BigDecimal.valueOf(matric));
        m.put("aprobados", BigDecimal.valueOf(aprob));
        m.put("rendimiento", BigDecimal.valueOf(rend));
        return m;
    }

    // ======== TESTS ========
    @Test
    void get_showAsignatura_devuelveVista() {
        Model model = new ExtendedModelMap();
        String vista = controller.showAsignatura(model);
        assertEquals("showAsignatura", vista);
        assertTrue(((ExtendedModelMap) model).isEmpty());
    }

    @Test
    void post_mostrarEvolucion_asignaturaExacta() {
        Model model = new ExtendedModelMap();
        String vista = controller.mostrarEvolucion("Matemáticas", model);
        assertEquals("showAsignatura", vista);
        assertEquals("Matemáticas", ((ExtendedModelMap) model).get("seleccionada"));
        assertNotNull(((ExtendedModelMap) model).get("evolucion"));
        assertNotNull(((ExtendedModelMap) model).get("labels"));
        assertNotNull(((ExtendedModelMap) model).get("rendimientos"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> evo = (List<Map<String, Object>>) ((ExtendedModelMap) model).get("evolucion");
        assertEquals(2, evo.size(), "Debe cargar la evolución preparada en el stub");
    }

    @Test
    void post_mostrarEvolucion_sinCoincidencias_mensaje() {
        Model model = new ExtendedModelMap();
        String vista = controller.mostrarEvolucion("XYZ", model);
        assertEquals("showAsignatura", vista);
        assertEquals("XYZ", ((ExtendedModelMap) model).get("seleccionada"));
        assertTrue(((ExtendedModelMap) model).containsAttribute("mensaje"));
        assertNull(((ExtendedModelMap) model).get("evolucion"));
    }

    @Test
    void post_mostrarEvolucion_unaCoincidencia_resuelveYEjecutaRender() {
        Model model = new ExtendedModelMap();
        String vista = controller.mostrarEvolucion("Progra", model);
        assertEquals("showAsignatura", vista);
        assertEquals("Programación", ((ExtendedModelMap) model).get("seleccionada"));
        assertNotNull(((ExtendedModelMap) model).get("evolucion"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> evo = (List<Map<String, Object>>) ((ExtendedModelMap) model).get("evolucion");
        assertEquals(1, evo.size());
    }

    @Test
    void post_mostrarEvolucion_variasCoincidencias_pideElegir() {
        Model model = new ExtendedModelMap();
        String vista = controller.mostrarEvolucion("MAT", model);
        assertEquals("showAsignatura", vista);
        assertTrue(((ExtendedModelMap) model).containsAttribute("coincidencias"));
        assertEquals("Hay varias asignaturas que coinciden. Elige una de la lista.", ((ExtendedModelMap) model).get("mensaje"));
    }

    @Test
    void post_mostrarEvolucion_valoresEspeciales_null_o_blanco() {
        // null
        Model modelNull = new ExtendedModelMap();
        String vistaNull = controller.mostrarEvolucion(null, modelNull);
        assertEquals("showAsignatura", vistaNull);
        assertTrue(((ExtendedModelMap) modelNull).containsAttribute("mensaje"));
        // cadena vacía
        Model modelBlanco = new ExtendedModelMap();
        String vistaBlanco = controller.mostrarEvolucion("   ", modelBlanco);
        assertEquals("showAsignatura", vistaBlanco);
        assertTrue(((ExtendedModelMap) modelBlanco).containsAttribute("mensaje"));
    }

    @Test
    void api_buscarAsignaturas_devuelveLista() {
        var lista = controller.buscarAsignaturas("Progra");
        assertEquals(List.of("Programación"), lista);
    }

    /**
     * Interfaz que mapea los métodos usados por el controlador.
     * La clase real (ShowAsignaturaRepository) tiene los mismos métodos públicos.
     * Se usa para no arrastrar dependencias de Spring/Jdbc en estas pruebas estructurales.
     */
    interface ShowAsignaturaRepositoryContract {
        List<String> buscarAsignaturas(String q);

        boolean existeAsignaturaExacta(String nombre);

        List<Map<String, Object>> obtenerEvolucion(String asignatura);
    }

    // ===== Stub de repositorio SIN mocks =====
    static class RepoStub implements ShowAsignaturaRepositoryContract {
        // Base de datos en memoria
        final Set<String> exactas = new HashSet<>();
        final Map<String, List<String>> porSubcadena = new HashMap<>();
        final Map<String, List<Map<String, Object>>> evoluciones = new HashMap<>();

        @Override
        public boolean existeAsignaturaExacta(String nombre) {
            return nombre != null && exactas.contains(nombre);
        }

        @Override
        public List<String> buscarAsignaturas(String q) {
            if (q == null) return List.of();
            return porSubcadena.getOrDefault(q, List.of());
        }

        @Override
        public List<Map<String, Object>> obtenerEvolucion(String asignatura) {
            return evoluciones.getOrDefault(asignatura, List.of());
        }
    }

    // Adaptador simple para que el controlador reciba cualquier implementación con la misma forma que su repositorio real.
    static class RepoAdapter extends ShowAsignaturaRepository {
        private final ShowAsignaturaRepositoryContract backing;

        public RepoAdapter(ShowAsignaturaRepositoryContract backing) {
            super(null);
            this.backing = backing;
        }

        @Override
        public List<String> buscarAsignaturas(String q) {
            return backing.buscarAsignaturas(q);
        }

        @Override
        public boolean existeAsignaturaExacta(String nombre) {
            return backing.existeAsignaturaExacta(nombre);
        }

        @Override
        public List<Map<String, Object>> obtenerEvolucion(String asignatura) {
            return backing.obtenerEvolucion(asignatura);
        }
    }
}