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
import java.util.*;

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

        List<String> cabecera = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea = reader.readLine();
            if (linea == null) {
                model.addAttribute("mensaje", "El CSV está vacío");
                return "importar";
            }

            // Cabecera -> lista de columnas (split básico por ahora)
            cabecera = parseCSVLine(linea);

            // PASO 1: Crear la tabla con estas columnas
            repository.crearTablaDesdeCabecera(cabecera);

            model.addAttribute("mensaje",
                    "Tabla creada con " + cabecera.size() + " columnas.");
        }

        // 1) verificar/crear tabla
        //repository.crearTablaSiNoExiste();



// 2) Leer CSV → construir columnasIncluidas (cabecera) y filas (datos estructurados)
        List<String> columnasIncluidas = new ArrayList<>();
        List<Map<String, String>> filas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            // Leer cabecera
            String headerLine = reader.readLine();
            if (headerLine == null) {
                model.addAttribute("mensaje", "El CSV está vacío");
                return "importar";
            }

            columnasIncluidas = parseCSVLine(headerLine);  // List<String>

            // Crear tabla según cabecera
            repository.crearTablaDesdeCabecera(columnasIncluidas);

            // Leer filas
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;

                List<String> valores = parseCSVLine(linea);

                // Fila estructurada y en orden
                Map<String, String> fila = new LinkedHashMap<>();

                for (int i = 0; i < Math.min(valores.size(), columnasIncluidas.size()); i++) {
                    fila.put(columnasIncluidas.get(i), valores.get(i));
                }

                filas.add(fila); // List<Map<String,String>>
            }
        }

// 3) Insertar filas si no existen
        int insertadas = repository.insertarFilasSiNoExistenBatch(
                filas,               // List<Map<String,String>>
                columnasIncluidas,   // List<String>
                200                  // batch size
        );

        int candidatas = filas.size();
        int omitidas = candidatas - insertadas;

        model.addAttribute(
                "mensaje",
                "Importación realizada. Candidatas: " + candidatas +
                        ", insertadas nuevas: " + insertadas +
                        ", ya existentes (omitidas): " + omitidas
        );


        return "importar";
    }

    // Parser
    private static List<String> parseCSVLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null) return out;

        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        out.add(sb.toString());
        return out;
    }
}