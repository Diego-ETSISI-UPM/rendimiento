package es.upm.etsisi.bg0272.tfg.rendimiento;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pruebas funcionales mínimas para cubrir la clase Main.
 * - Arranca el contexto de Spring (sin servidor web).
 * - Invoca el método main() pasando el parámetro para no levantar el servidor.
 * <p>
 * Nota: RendimientoApplication contiene un main que delega en SpringApplication.run(...).
 * Estas pruebas confirman que el contexto carga y que el entrypoint es invocable
 * sin efectos secundarios de red.
 */
class RendimientoApplicationTests {

    /**
     * Invoca el método main(String[]) con un argumento que desactiva el servidor web.
     * Con esto cubrimos también el entrypoint sin levantar Tomcat/Jetty.
     */
    @Test
    void main_isInvocable_withoutWebServer() {
        assertDoesNotThrow(() ->
                        RendimientoApplication.main(new String[]{
                                "--spring.main.web-application-type=none",
                                "--spring.main.banner-mode=off"
                        }),
                "La invocación de main() no debería lanzar excepciones"
        );
    }

    /**
     * Arranque de contexto sin servidor web.
     * Usa el classpath real del proyecto y valida que el contenedor de Spring se construye.
     */
    @SpringBootTest(
            classes = RendimientoApplication.class,
            webEnvironment = SpringBootTest.WebEnvironment.NONE
    )
    @ActiveProfiles("test")
    static class ContextLoadsTest {

        @Autowired
        ApplicationContext context;

        @Test
        void contextLoads() {
            assertNotNull(context, "El ApplicationContext debería haberse creado");
        }
    }
}