package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ShowAsignaturaRepository {

    private final JdbcTemplate jdbc;

    public ShowAsignaturaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<String> obtenerListaAsignaturas() {
        String sql = "SELECT DISTINCT asignatura FROM datos_csv ORDER BY asignatura";
        var lista = jdbc.queryForList(sql, String.class);
        System.out.println("[DEBUG] Asignaturas disponibles: " + lista.size());
        return lista;
    }

    public List<Map<String, Object>> obtenerEvolucion(String asignatura) {
        String sql = """
            SELECT 
                ano_academico,
                SUM(COALESCE(n_matriculados, 0)) AS total_matriculados,
                SUM(COALESCE(n_aprobados,   0)) AS total_aprobados,
                CASE 
                    WHEN SUM(COALESCE(n_matriculados, 0)) = 0 THEN 0
                    ELSE ROUND(SUM(COALESCE(n_aprobados, 0)) / SUM(COALESCE(n_matriculados, 0)) * 100, 2)
                END AS rendimiento_real
            FROM datos_csv
            WHERE asignatura = ?
            GROUP BY ano_academico
            ORDER BY CAST(SUBSTRING(ano_academico, 1, 4) AS UNSIGNED)
        """;

        return jdbc.query(
                sql,
                ps -> ps.setString(1, asignatura),
                (rs, rowNum) -> {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("ano_academico", rs.getString("ano_academico"));
                    fila.put("matriculados", rs.getBigDecimal("total_matriculados"));
                    fila.put("aprobados", rs.getBigDecimal("total_aprobados"));
                    fila.put("rendimiento", rs.getBigDecimal("rendimiento_real"));
                    return fila;
                }
        );
    }

    public boolean existeAsignaturaExacta(String nombre) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM datos_csv WHERE asignatura = ?",
                Integer.class,
                nombre
        );
        return count != null && count > 0;
    }

    public List<String> buscarAsignaturas(String q) {
        String sql = """
            SELECT asignatura 
            FROM datos_csv
            WHERE asignatura LIKE CONCAT('%', ?, '%')
            GROUP BY asignatura
            ORDER BY asignatura            
        """;
        return jdbc.queryForList(sql, String.class, q);
    }
}