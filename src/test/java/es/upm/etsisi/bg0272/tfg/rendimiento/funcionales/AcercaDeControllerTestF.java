package es.upm.etsisi.bg0272.tfg.rendimiento.funcionales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.AcercaDeController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcercaDeControllerTestF {
    @Test
    void acercaDe_devuelveVista() {
        var controller = new AcercaDeController();
        String vista = controller.acercaDe();
        assertEquals("acercaDe", vista);
    }
}