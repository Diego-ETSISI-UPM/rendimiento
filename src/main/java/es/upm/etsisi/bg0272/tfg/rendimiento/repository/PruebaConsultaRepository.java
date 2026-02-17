package es.upm.etsisi.bg0272.tfg.rendimiento.repository;
import es.upm.etsisi.bg0272.tfg.rendimiento.model.TablaPruebaRegistro;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class PruebaConsultaRepository {
    private final JdbcTemplate jdbcTemplate;
    public PruebaConsultaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public String obtenerTexto() {
        return jdbcTemplate.queryForObject(
                "SELECT texto FROM prueba WHERE id = 2",
                String.class
        );
    }
    public List<TablaPruebaRegistro> obtenerTodos() {
        return jdbcTemplate.query(
                "SELECT id, texto FROM prueba",
                (rs, rowNum) ->
                        new TablaPruebaRegistro(
                                rs.getInt("id"),
                                rs.getString("texto")
                        )
        );
    }
}