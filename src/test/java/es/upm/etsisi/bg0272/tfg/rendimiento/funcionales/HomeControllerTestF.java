package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.HomeController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTestF {
    @Test
    void empty_devuelveHome() {
        var controller = new HomeController();
        String vista = controller.empty();
        assertEquals("home", vista);
    }

    @Test
    void home_devuelveHome() {
        var controller = new HomeController();
        String vista = controller.home();
        assertEquals("home", vista);
    }
}
