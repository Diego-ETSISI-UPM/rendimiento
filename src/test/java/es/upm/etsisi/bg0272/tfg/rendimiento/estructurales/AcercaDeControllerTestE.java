package es.upm.etsisi.bg0272.tfg.rendimiento.estructurales;

import es.upm.etsisi.bg0272.tfg.rendimiento.controllers.AcercaDeController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcercaDeControllerTestE {
    @Test
    void acercaDe_devuelveVistaCorrecta() {
        AcercaDeController controller = new AcercaDeController();
        String vista = controller.acercaDe();
        assertEquals("acercaDe", vista);
    }
}

