package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ShowComparativaRepository {

    private final JdbcTemplate jdbc;

    public ShowComparativaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Devuelve, para cada asignatura pedida, su rendimiento histórico:
     *  rendimiento = SUM(n_aprobados) / SUM(n_matriculados) * 100
     * Ordenado de menor a mayor rendimiento.
     */
    public List<Map<String, Object>> obtenerRendimientoComparativa(List<String> asignaturas) {
        if (asignaturas == null || asignaturas.isEmpty()) {
            return Collections.emptyList();
        }
        // Placeholder dinámico para IN (?, ?, ?, ...)
        String placeholders = String.join(",", Collections.nCopies(asignaturas.size(), "?"));

        String sql = """
            SELECT
                asignatura,
                SUM(COALESCE(n_matriculados, 0)) AS total_matriculados,
                SUM(COALESCE(n_aprobados,   0)) AS total_aprobados,
                CASE
                  WHEN SUM(COALESCE(n_matriculados, 0)) = 0 THEN 0
                  ELSE ROUND(SUM(COALESCE(n_aprobados, 0)) / SUM(COALESCE(n_matriculados, 0)) * 100, 2)
                END AS rendimiento
            FROM datos_csv
            WHERE asignatura IN (""" + placeholders + ") " + """
            GROUP BY asignatura
            ORDER BY rendimiento ASC, asignatura ASC
        """;

        return jdbc.query(
                sql,
                asignaturas.toArray(),
                (rs, rowNum) -> {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("asignatura",   rs.getString("asignatura"));
                    fila.put("matriculados", rs.getBigDecimal("total_matriculados"));
                    fila.put("aprobados",    rs.getBigDecimal("total_aprobados"));
                    fila.put("rendimiento",  rs.getBigDecimal("rendimiento"));
                    return fila;
                }
        );
    }
}