package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ImportRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportRepositoryTestF {
    // -------- TESTS --------
    @Test
    void funcional_crearTabla() {
        var stub = new Stub();
        var repo = new RepoAdapter(stub);
        repo.crearTablaDesdeCabecera(List.of("a", "b", "c"));
        assertEquals(List.of("a", "b", "c"), stub.columnas);
    }

    @Test
    void funcional_insertar() {
        var stub = new Stub();
        var repo = new RepoAdapter(stub);
        var filas = List.of(Map.of("a", "1"), Map.of("a", "2"));
        int r = repo.insertarFilasSiNoExistenBatch(filas, List.of("a"), 200);
        assertEquals(2, r);
        assertEquals(2, stub.filas.size());
    }

    // -------- CONTRACT --------
    interface Contract {
        void crear(List<String> columnas);

        int insertar(List<Map<String, String>> filas, List<String> columnas, int batch);
    }

    // -------- STUB --------
    static class Stub implements Contract {
        List<String> columnas = new ArrayList<>();
        List<Map<String, String>> filas = new ArrayList<>();

        @Override
        public void crear(List<String> c) {
            columnas = new ArrayList<>(c);
        }

        @Override
        public int insertar(List<Map<String, String>> f, List<String> c, int batch) {
            filas.addAll(f);
            return f.size();
        }
    }

    // -------- ADAPTER --------
    static class RepoAdapter extends ImportRepository {
        private final Contract b;

        public RepoAdapter(Contract b) {
            super(null);
            this.b = b;
        }

        @Override
        public void crearTablaDesdeCabecera(List<String> c) {
            b.crear(c);
        }

        @Override
        public int insertarFilasSiNoExistenBatch(List<Map<String, String>> f, List<String> col, int bSize) {
            return b.insertar(f, col, bSize);
        }
    }
}
