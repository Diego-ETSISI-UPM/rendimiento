package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShowCSVRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowCSVRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean tablaExiste() {
        String sql = """
            SELECT COUNT(*)
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
              AND table_name = 'datos_csv'
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }

    // Devuelve cada fila como una línea de texto (concatenada)
    public List<String> obtenerLineas() {
        return jdbcTemplate.query(
                "SELECT * FROM datos_csv",
                (rs, rowNum) -> {
                    int colCount = rs.getMetaData().getColumnCount();
                    StringBuilder sb = new StringBuilder();

                    for (int i = 1; i <= colCount; i++) {
                        if (i > 1) sb.append(" | ");
                        sb.append(rs.getMetaData().getColumnName(i))
                                .append("=")
                                .append(rs.getString(i));
                    }
                    return sb.toString();
                }
        );
    }
}