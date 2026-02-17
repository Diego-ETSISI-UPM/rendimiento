package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ImportRepository {
    private final JdbcTemplate jdbcTemplate;
    public ImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void crearTablaDesdeCabecera(List<String> columnas) {

        /*/ Columnas que NO queremos incluir en la tabla
        Set<String> columnasExcluidas = Set.of(
                "Aprobados en 1ª Mat",
                "Matriculados por 1ª vez",
                "Rendimiento en 1ª Mat",
                "Aprobados en 2ª Mat",
                "Matriculados por 2ª vez",
                "Rendimiento en 2ª Mat",
                "Aprobados en 3ª Mat",
                "Matriculados por 3ª vez o más",
                "Rendimiento en 3ª Mat"
        );*/

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `datos_csv` (\n");
        sb.append("  `id` INT AUTO_INCREMENT PRIMARY KEY");

        // Añadimos una coma solo si hay columnas válidas
        boolean primera = true;

        for (String col : columnas) {

            /*/ saltar columnas redundantes
            if (columnasExcluidas.contains(col.trim())) {
                continue;
            }*/

            sb.append(",\n  `").append(col).append("` VARCHAR(255)");
        }

        sb.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        jdbcTemplate.execute(sb.toString());
    }

    public int insertarFilasSiNoExistenBatch(List<Map<String, String>> filas, List<String> columnasIncluidas, int batchSize) {

        if (filas == null || filas.isEmpty() || columnasIncluidas == null || columnasIncluidas.isEmpty()) {
            return 0;
        }

        String columnasSQL = columnasIncluidas.stream()
                .map(c -> "`" + c + "`")
                .collect(Collectors.joining(", "));

        String placeholders = columnasIncluidas.stream()
                .map(c -> "?")
                .collect(Collectors.joining(", "));

        String where = columnasIncluidas.stream()
                .map(c -> "`" + c + "` = ?")
                .collect(Collectors.joining(" AND "));

        String sql = """
        INSERT INTO `datos_csv` (%s)
        SELECT %s
        WHERE NOT EXISTS (
            SELECT 1 FROM `datos_csv`
            WHERE %s
        )
        """.formatted(columnasSQL, placeholders, where);

        int[][] counts = jdbcTemplate.batchUpdate(
                sql,
                filas,
                batchSize,
                (ps, fila) -> {
                    int idx = 1;
                    // SELECT ?
                    for (String c : columnasIncluidas) {
                        ps.setString(idx++, fila.get(c));
                    }
                    // WHERE ?
                    for (String c : columnasIncluidas) {
                        ps.setString(idx++, fila.get(c));
                    }
                }
        );

        // Sumar filas realmente insertadas (MySQL devuelve 1 cuando inserta, 0 si NOT EXISTS bloquea)
        int total = 0;
        for (int[] lote : counts) {
            for (int c : lote) total += c;
        }
        return total;
    }

}
