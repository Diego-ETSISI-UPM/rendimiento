package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.ImportController;
import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ImportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportControllerTestE {
    // ---------------- TESTS ----------------
    @Test
    void get_importar_devuelveVista() {
        var repo = new RepoStub();
        var controller = new ImportController(new RepoAdapter(repo));
        assertEquals("importar", controller.importar());
    }

    @Test
    void post_importar_archivoVacio() throws Exception {
        var repo = new RepoStub();
        var controller = new ImportController(new RepoAdapter(repo));
        Model model = new ExtendedModelMap();
        MockMultipartFile file = new MockMultipartFile("archivo", new byte[0]);
        String vista = controller.ejecutarImportacion(file, model);
        assertEquals("importar", vista);
        assertEquals("No se ha seleccionado ningún archivo", model.getAttribute("mensaje"));
    }

    @Test
    void post_importar_csvSinCabecera() throws Exception {
        var repo = new RepoStub();
        var controller = new ImportController(new RepoAdapter(repo));
        Model model = new ExtendedModelMap();
        // Archivo vacío → activa archivo.isEmpty()
        MockMultipartFile file =
                new MockMultipartFile("archivo", new byte[0]);
        String vista = controller.ejecutarImportacion(file, model);
        assertEquals("importar", vista);
        assertEquals("No se ha seleccionado ningún archivo",
                model.getAttribute("mensaje"));
    }

    @Test
    void post_importar_csvOk() throws Exception {
        var repo = new RepoStub();
        var controller = new ImportController(new RepoAdapter(repo));
        Model model = new ExtendedModelMap();
        String csv = "Asignatura,Curso,Nota final\n" + "Matemáticas,1,7.2\n" + "Física,1,6.1\n";
        MockMultipartFile file = new MockMultipartFile("archivo", csv.getBytes(StandardCharsets.UTF_8));
        String vista = controller.ejecutarImportacion(file, model);
        assertEquals("importar", vista);
        String mensaje = model.getAttribute("mensaje").toString();
        assertTrue(mensaje.contains("Importación realizada"));
        assertEquals(List.of("asignatura", "curso", "nota_final"), repo.columnasCreadas);
        assertEquals(2, repo.filasInsertadas.size());
    }

    interface ImportRepoContract {
        void crearTablaDesdeCabecera(List<String> columnas);

        int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas,
                                          List<String> columnasIncluidas,
                                          int batchSize);
    }

    static class RepoStub implements ImportRepoContract {
        List<String> columnasCreadas = new ArrayList<>();
        List<Map<String, String>> filasInsertadas = new ArrayList<>();

        @Override
        public void crearTablaDesdeCabecera(List<String> columnas) {
            columnasCreadas = new ArrayList<>(columnas);
        }

        @Override
        public int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas, List<String> columnasIncluidas, int batchSize) {
            filasInsertadas.addAll(filas);
            return filas.size();
        }
    }

    static class RepoAdapter extends ImportRepository {
        private final ImportRepoContract backing;

        public RepoAdapter(ImportRepoContract backing) {
            super(null);
            this.backing = backing;
        }

        @Override
        public void crearTablaDesdeCabecera(List<String> columnas) {
            backing.crearTablaDesdeCabecera(columnas);
        }

        @Override
        public int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas, List<String> columnasIncluidas, int batchSize) {
            return backing.insertarFilasSiNoExistenBatch(filas, columnasIncluidas, batchSize);
        }
    }
}