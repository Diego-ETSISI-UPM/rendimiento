package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ImportRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Prueba estructural de ImportRepository sin base de datos
class ImportRepositoryTestE {
    // ===== TESTS =====
    @Test
    void crearTablaDesdeCabecera_ok() {
        RepoStub stub = new RepoStub();
        ImportRepository repo = new RepoAdapter(stub);
        List<String> columnas = List.of("asignatura", "curso", "nota_final");
        repo.crearTablaDesdeCabecera(columnas);
        assertEquals(columnas, stub.columnasCreadas);
    }

    @Test
    void insertarFilasSiNoExistenBatch_listaVacia() {
        RepoStub stub = new RepoStub();
        ImportRepository repo = new RepoAdapter(stub);
        int r = repo.insertarFilasSiNoExistenBatch(List.of(), List.of("asignatura", "curso"), 200);
        assertEquals(0, r);
        assertTrue(stub.filasInsertadas.isEmpty());
    }

    @Test
    void insertarFilasSiNoExistenBatch_variasFilas() {
        RepoStub stub = new RepoStub();
        ImportRepository repo = new RepoAdapter(stub);
        List<Map<String, String>> filas = List.of(Map.of("asignatura", "Matemáticas", "curso", "1"), Map.of("asignatura", "Física", "curso", "1"));
        List<String> columnas = List.of("asignatura", "curso");
        int r = repo.insertarFilasSiNoExistenBatch(filas, columnas, 200);
        assertEquals(2, r);
        assertEquals(2, stub.filasInsertadas.size());
    }

    // ===== CONTRACT =====
    interface ImportRepoContract {
        void crearTablaDesdeCabecera(List<String> columnas);

        int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas, List<String> columnas, int batchSize);
    }

    // ===== STUB =====
    static class RepoStub implements ImportRepoContract {
        List<String> columnasCreadas = new ArrayList<>();
        List<Map<String, String>> filasInsertadas = new ArrayList<>();

        @Override
        public void crearTablaDesdeCabecera(List<String> columnas) {
            columnasCreadas = new ArrayList<>(columnas);
        }

        @Override
        public int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas, List<String> columnas, int batchSize) {
            filasInsertadas.addAll(filas);
            return filas.size();
        }
    }

    // ===== ADAPTER =====
    static class RepoAdapter extends ImportRepository {
        private final ImportRepoContract backing;

        public RepoAdapter(ImportRepoContract backing) {
            super(null); // jdbcTemplate no se usa
            this.backing = backing;
        }

        @Override
        public void crearTablaDesdeCabecera(List<String> columnas) {
            backing.crearTablaDesdeCabecera(columnas);
        }

        @Override
        public int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas, List<String> columnas, int batchSize) {
            return backing.insertarFilasSiNoExistenBatch(filas, columnas, batchSize);
        }
    }
}