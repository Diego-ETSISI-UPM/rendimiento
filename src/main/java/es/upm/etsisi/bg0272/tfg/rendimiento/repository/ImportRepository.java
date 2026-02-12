package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public void insertarLineas(List<String> lineas) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO datos_csv (linea) VALUES (?)",
                lineas,
                100,
                (ps, linea) -> ps.setString(1, linea)
        );
    }
}
