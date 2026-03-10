package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.HomeController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTestE {
    @Test
    void empty_devuelveHome() {
        HomeController controller = new HomeController();
        String vista = controller.empty();
        assertEquals("home", vista);
    }

    @Test
    void home_devuelveHome() {
        HomeController controller = new HomeController();
        String vista = controller.home();
        assertEquals("home", vista);
    }
}