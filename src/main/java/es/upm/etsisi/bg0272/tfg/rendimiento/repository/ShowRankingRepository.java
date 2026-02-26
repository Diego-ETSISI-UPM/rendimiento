package es.upm.etsisi.bg0272.tfg.rendimiento.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ShowRankingRepository {

    private final JdbcTemplate jdbc;

    public ShowRankingRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ► 1) Obtener todos los planes disponibles
    public List<String> obtenerPlanes() {
        String sql = """
            SELECT DISTINCT plan_de_estudios
            FROM datos_csv
            WHERE plan_de_estudios IS NOT NULL AND plan_de_estudios <> ''
            ORDER BY plan_de_estudios
        """;
        return jdbc.queryForList(sql, String.class);
    }

    // ► 2) Obtener asignaturas del plan seleccionado (para el autocompletado)
    public List<String> buscarAsignaturasDePlan(String plan, String q) {
        String sql = """
            SELECT DISTINCT asignatura
            FROM datos_csv
            WHERE plan_de_estudios = ?
              AND asignatura LIKE CONCAT('%', ?, '%')
            ORDER BY asignatura
            LIMIT 20
        """;
        return jdbc.queryForList(sql, String.class, plan, q);
    }

    // ► 3) Ranking del último año académico de cada asignatura seleccionada
    public List<Map<String,Object>> rankingUltimoAnio(String plan, List<String> asignaturas) {

        if (asignaturas == null || asignaturas.isEmpty())
            return Collections.emptyList();

        String placeholders = String.join(",", Collections.nCopies(asignaturas.size(), "?"));

        String sql = """
        SELECT  
            t.asignatura,
            x.max_anio AS ano_academico,
            SUM(t.n_matriculados) AS total_matriculados,
            SUM(t.n_aprobados) AS total_aprobados,
            CASE 
                WHEN SUM(t.n_matriculados) = 0 THEN 0
                ELSE ROUND(SUM(t.n_aprobados) / SUM(t.n_matriculados) * 100, 2)
            END AS rendimiento
        FROM datos_csv t
        JOIN (
            SELECT asignatura,
                   MAX(CAST(SUBSTRING(ano_academico, 1, 4) AS UNSIGNED)) AS max_anio
            FROM datos_csv
            WHERE plan_de_estudios = ?
            GROUP BY asignatura
        ) x
          ON t.asignatura = x.asignatura
         AND CAST(SUBSTRING(t.ano_academico, 1, 4) AS UNSIGNED) = x.max_anio
        WHERE t.plan_de_estudios = ?
          AND t.asignatura IN (""" + placeholders + """
        )
        GROUP BY t.asignatura, x.max_anio
        ORDER BY rendimiento ASC, t.asignatura ASC
        """;

        List<Object> params = new ArrayList<>();
        params.add(plan);  // subconsulta
        params.add(plan);  // consulta principal
        params.addAll(asignaturas);

        return jdbc.query(
                sql,
                params.toArray(),
                (rs, rowNum) -> {
                    Map<String,Object> fila = new LinkedHashMap<>();
                    fila.put("asignatura", rs.getString("asignatura"));
                    fila.put("ano", rs.getString("ano_academico"));
                    fila.put("matriculados", rs.getBigDecimal("total_matriculados"));
                    fila.put("aprobados", rs.getBigDecimal("total_aprobados"));
                    fila.put("rendimiento", rs.getBigDecimal("rendimiento"));
                    return fila;
                }
        );
    }
}