package es.upm.etsisi.bg0272.tfg.rendimiento.controllers;

import es.upm.etsisi.bg0272.tfg.rendimiento.repository.ImportRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

@Controller
public class ImportController {

    private final ImportRepository repository;

    public ImportController(ImportRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/importar")
    public String importarPage() {
        return "importar";
    }

    @PostMapping("/importar")
    public String ejecutarImportacion(@RequestParam("archivo") MultipartFile archivo, Model model) throws Exception {
        if (archivo.isEmpty()) {
            model.addAttribute("mensaje", "No se ha seleccionado ningún archivo");
            return "importar";
        }

        // 1) Asegurar tabla
        repository.crearTablaSiNoExiste();

        // 2) Leer CSV -> normalizar básico (trim) + filtrar vacías
        Set<String> únicasEnFichero = new LinkedHashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String limpia = linea.trim();
                if (!limpia.isEmpty()) {
                    únicasEnFichero.add(limpia);
                }
            }
        }

        // 3) Insertar solo las que no existan ya en BD
        int insertadas = repository.insertarLineasSiNoExisten(únicasEnFichero.stream().toList());

        int candidatas = únicasEnFichero.size();
        int saltadasPorDuplicado = candidatas - insertadas;

        model.addAttribute(
                "mensaje",
                "Importación realizada. Candidatas: " + candidatas +
                        ", insertadas nuevas: " + insertadas +
                        ", ya existentes (omitidas): " + saltadasPorDuplicado
        );

        return "importar";
    }
}