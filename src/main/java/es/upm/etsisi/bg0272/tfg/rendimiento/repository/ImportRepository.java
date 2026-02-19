package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ImportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // === Declaración de columnas numéricas (normalizadas) ===
    private static final Set<String> NUMERIC_DECIMAL_0 = Set.of(
            "numero_de_matriculas",
            "n_matriculados",
            "n_aprobados",
            "curso"
    );
    private static final Set<String> NUMERIC_DECIMAL_2 = Set.of(
            "rendimiento",
            "eficiencia_en_matricula"
    );
    private static final Map<String, String> COLUMN_TYPES;
    static {
        Map<String, String> m = new HashMap<>();
        NUMERIC_DECIMAL_0.forEach(c -> m.put(c, "DECIMAL(10,0)"));
        NUMERIC_DECIMAL_2.forEach(c -> m.put(c, "DECIMAL(18,2)"));
        COLUMN_TYPES = Collections.unmodifiableMap(m);
    }

    // === Creación de tabla con clave primaria compuesta ===
    public void crearTablaDesdeCabecera(List<String> columnas) {

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `datos_csv` (\n");

        boolean first = true;

        for (String col : columnas) {
            String tipo = COLUMN_TYPES.getOrDefault(col, "VARCHAR(255)");
            if (!first) sb.append(",\n");
            first = false;
            sb.append("  `").append(col).append("` ").append(tipo);
        }

        // PRIMARY KEY compuesta
        sb.append(",\n  PRIMARY KEY (`ano_academico`, `numero_de_matriculas`, `asignatura`)");
        sb.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        jdbcTemplate.execute(sb.toString());
    }

    // === Inserción con ON DUPLICATE KEY UPDATE ===
    public int insertarFilasSiNoExistenBatch(
            List<Map<String, String>> filas,
            List<String> columnasIncluidas,
            int batchSize) {

        if (filas == null || filas.isEmpty()) return 0;

        String columnasSQL = columnasIncluidas.stream()
                .map(c -> "`" + c + "`")
                .collect(Collectors.joining(", "));

        String placeholdersSQL = columnasIncluidas.stream()
                .map(c -> "?")
                .collect(Collectors.joining(", "));

        String updateSQL = columnasIncluidas.stream()
                .filter(c -> !Set.of("ano_academico", "numero_de_matriculas", "asignatura").contains(c))
                .map(c -> "`" + c + "` = VALUES(`" + c + "`)")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO `datos_csv` (" + columnasSQL + ") VALUES (" + placeholdersSQL + ")\n"
                + "ON DUPLICATE KEY UPDATE " + updateSQL;

        int[][] counts = jdbcTemplate.batchUpdate(
                sql,
                filas,
                batchSize,
                (ps, fila) -> {
                    int idx = 1;
                    for (String col : columnasIncluidas) {
                        String raw = fila.get(col);
                        if (COLUMN_TYPES.containsKey(col)) {
                            if (raw == null || raw.isBlank()) {
                                ps.setNull(idx++, Types.DECIMAL);
                            } else {
                                ps.setBigDecimal(idx++, new BigDecimal(raw.replace(",", ".")));
                            }
                        } else {
                            ps.setString(idx++, raw);
                        }
                    }
                }
        );

        int total = 0;
        for (int[] lote : counts)
            for (int c : lote)
                total += (c >= 0 ? 1 : 0);

        return total;
    }
}