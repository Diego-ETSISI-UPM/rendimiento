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
import java.text.Normalizer;
import java.util.*;

@Controller
public class ImportController {

    // Columnas que NO queremos incluir
    private static final Set<String> columnasExcluidas = Set.of(
            "Aprobados en 1ª Mat",
            "Matriculados por 1ª vez",
            "Rendimiento en 1ª Mat",
            "Aprobados en 2ª Mat",
            "Matriculados por 2ª vez",
            "Rendimiento en 2ª Mat",
            "Aprobados en 3ª Mat",
            "Matriculados por 3ª vez o más",
            "Rendimiento en 3ª Mat"
    );

    private final ImportRepository repository;

    public ImportController(ImportRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/importar")
    public String importar() {
        return "importar";
    }

    @PostMapping("/importar")
    public String ejecutarImportacion(@RequestParam("archivo") MultipartFile archivo,
                                      Model model) throws Exception {

        if (archivo.isEmpty()) {
            model.addAttribute("mensaje", "No se ha seleccionado ningún archivo");
            return "importar";
        }

        List<String> cabeceraOriginal;
        List<String> cabeceraFiltrada;
        LinkedHashMap<String, String> headerMap;
        List<String> columnasNormalizadas;
        List<Map<String, String>> filas = new ArrayList<>();

        // --- 1) Leer cabecera, filtrar y normalizar nombres ---
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                model.addAttribute("mensaje", "El CSV está vacío");
                return "importar";
            }

            cabeceraOriginal = parseCSVLine(headerLine);

            // Filtrar columnas redundantes
            cabeceraFiltrada = cabeceraOriginal.stream()
                    .filter(col -> !columnasExcluidas.contains(col.trim()))
                    .toList();

            // Construir mapping original → normalizado
            headerMap = buildHeaderMappingPreservingOrder(cabeceraFiltrada);

            // Lista de columnas normalizadas
            columnasNormalizadas = new ArrayList<>(headerMap.values());

            // Crear tabla basada en columnas normalizadas
            repository.crearTablaDesdeCabecera(columnasNormalizadas);

            // --- Leer filas ---
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;

                List<String> valores = parseCSVLine(linea);
                Map<String, String> fila = new LinkedHashMap<>();

                for (int i = 0; i < Math.min(valores.size(), cabeceraOriginal.size()); i++) {
                    String colOrig = cabeceraOriginal.get(i).trim();
                    if (!headerMap.containsKey(colOrig)) continue;

                    String colNorm = headerMap.get(colOrig);
                    fila.put(colNorm, valores.get(i));
                }

                filas.add(fila);
            }
        }

        // --- 3) Insertar ---
        int insertadas = repository.insertarFilasSiNoExistenBatch(
                filas,
                columnasNormalizadas,
                200
        );

        int candidatas = filas.size();
        int omitidas = candidatas - insertadas;

        model.addAttribute("mensaje",
                "Importación realizada. Candidatas: " + candidatas +
                        ", insertadas nuevas: " + insertadas +
                        ", ya existentes: " + omitidas);

        return "importar";
    }


    // Normalizar nombres de columna
    private static String normalizeHeader(String raw) {
        if (raw == null) return "";

        String s = raw.trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // quitar tildes
        s = s.toLowerCase(Locale.ROOT);
        s = s.replaceAll("[\\s\\-–—]+", "_");
        s = s.replaceAll("[^a-z0-9_]", "");
        s = s.replaceAll("_+", "_").replaceAll("^_+|_+$", "");
        if (s.isEmpty()) s = "columna";

        return s;
    }

    // Resolver colisiones y mantener orden
    private static LinkedHashMap<String, String> buildHeaderMappingPreservingOrder(List<String> cabeceraOriginal) {
        LinkedHashMap<String, String> mapping = new LinkedHashMap<>();
        Map<String, Integer> seen = new HashMap<>();

        for (String raw : cabeceraOriginal) {
            String base = normalizeHeader(raw);
            int n = seen.getOrDefault(base, 0) + 1;
            seen.put(base, n);

            String normalized = (n == 1) ? base : base + "__" + n;
            mapping.put(raw.trim(), normalized);
        }
        return mapping;
    }

    // Parser CSV
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