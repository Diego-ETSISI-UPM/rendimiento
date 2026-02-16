package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ImportRepository {
    private final JdbcTemplate jdbcTemplate;
    public ImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void crearTablaSiNoExiste() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS datos_csv (
                id INT AUTO_INCREMENT PRIMARY KEY,
                linea TEXT
            )
        """);
    }

    public void crearTablaDesdeCabecera(List<String> columnas) {

        // Construimos cada columna como VARCHAR(255)
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `").append("datos_csv").append("` (\n");
        sb.append("  `id` INT AUTO_INCREMENT PRIMARY KEY,\n");

        for (int i = 0; i < columnas.size(); i++) {
            String col = columnas.get(i);

            // Backticks para permitir espacios y tildes
            sb.append("  `").append(col).append("` VARCHAR(255)");

            if (i < columnas.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        jdbcTemplate.execute(sb.toString());
    }

    public int insertarFilasSiNoExistenBatch(
            List<Map<String, String>> filas,
            List<String> columnasIncluidas,
            int batchSize) {

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
