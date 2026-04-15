package es.upm.etsisi.bg0272.tfg.rendimiento.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAnyException(Exception ex, Model model) {

        model.addAttribute(
                "errorMessage",
                "Se ha producido un error al procesar la petición. Revise los datos e inténtelo de nuevo."
        );

        return "home";
    }
}
