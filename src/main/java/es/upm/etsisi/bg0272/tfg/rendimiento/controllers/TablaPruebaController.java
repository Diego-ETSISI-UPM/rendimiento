package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.InterfazRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TablaPruebaController {

    private final InterfazRepository repository;

    public TablaPruebaController(InterfazRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/MySQLRendimientoTablaPrueba")
    public String pruebaDB(Model model) {
        String texto = repository.obtenerTexto(); // SELECT texto FROM prueba WHERE id=1
        model.addAttribute("mensaje", texto);
        String texto2 = repository.obtenerTexto2(); // SELECT texto FROM prueba WHERE id=2
        model.addAttribute("mensaje2", texto2);
        model.addAttribute("filas", repository.obtenerTodos());
        return "MySQLRendimientoTablaPrueba"; // muestra templates/MySQLRendimientoTablaPrueba.html
    }
}