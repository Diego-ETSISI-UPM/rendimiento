package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> obtenerLineas() {
        return jdbcTemplate.query(
                "SELECT * FROM datos_csv",
                (rs, rowNum) -> {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    int colCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= colCount; i++) {
                        String colName = rs.getMetaData().getColumnName(i);
                        fila.put(colName, rs.getObject(i));
                    }
                    return fila;
                }
        );
    }

    public List<String> obtenerCabeceras() {
        return jdbcTemplate.query(
                "SELECT * FROM datos_csv LIMIT 1",
                rs -> {
                    List<String> cabeceras = new ArrayList<>();
                    int colCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= colCount; i++) {
                        cabeceras.add(rs.getMetaData().getColumnName(i));
                    }
                    return cabeceras;
                }
        );
    }
}