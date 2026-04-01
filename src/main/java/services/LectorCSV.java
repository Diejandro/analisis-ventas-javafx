package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import models.Alumno;
import models.Producto; // IMPORTANTE: Nueva dependencia
import models.Venta;

/**
 * Lector de archivos CSV con detección automática de separador y mapeo a objetos de dominio.
 */
public class LectorCSV {
    
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ISO_LOCAL_DATE
    };

    public List<Venta> leerCSV(File archivo) throws IOException {
        if (archivo == null || !archivo.exists()) {
            throw new IllegalArgumentException("El archivo no existe o es nulo");
        }

        char separador = detectarSeparador(archivo);
        List<Venta> ventas = new ArrayList<>();
        
        try (FileReader fileReader = new FileReader(archivo);
             CSVReader csvReader = crearCSVReader(fileReader, separador)) {
            
            List<String[]> registros = csvReader.readAll();
            Map<String, Alumno> mapaAlumnos = new HashMap<>();
            
            for (int i = 0; i < registros.size(); i++) {
                String[] linea = registros.get(i);
                try {
                    // Mapeamos la línea a la nueva estructura
                    Venta venta = parsearVenta(linea, i + 2, mapaAlumnos); 
                    if (venta != null) {
                        ventas.add(venta);
                    }
                } catch (Exception e) {
                    System.err.println("Error en línea " + (i + 2) + ": " + e.getMessage());
                }
            }
        } catch (CsvException e) {
            throw new IOException("Error al parsear CSV: " + e.getMessage(), e);
        }
        
        return ventas;
    }

    private char detectarSeparador(File archivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String primeraLinea = reader.readLine();
            if (primeraLinea == null || primeraLinea.isEmpty()) return ',';

            int comas = contarOcurrencias(primeraLinea, ',');
            int puntosComas = contarOcurrencias(primeraLinea, ';');

            return (puntosComas >= comas) ? ';' : ',';
        }
    }

    private int contarOcurrencias(String texto, char caracter) {
        int contador = 0;
        for (char c : texto.toCharArray()) {
            if (c == caracter) contador++;
        }
        return contador;
    }

    private CSVReader crearCSVReader(FileReader fileReader, char separador) throws IOException {
        BufferedReader br = new BufferedReader(fileReader);
        br.mark(1500);
        String firstLine = br.readLine();
        boolean tieneEncabezado = false;

        if (firstLine != null) {
            String lower = firstLine.toLowerCase();
            // Detectamos si la primera línea es cabecera por el CIF o Producto
            tieneEncabezado = lower.contains("cif") || lower.contains("producto") || lower.contains("email");
        }

        if (!tieneEncabezado) br.reset();

        CSVParser parser = new CSVParserBuilder().withSeparator(separador).build();
        return new CSVReaderBuilder(br)
                .withSkipLines(tieneEncabezado ? 1 : 0)
                .withCSVParser(parser)
                .build();
    }

    /**
     * Parsea la venta respetando el nuevo orden:
     * 0:CIF, 1:Nombre, 2:Email, 3:ID_Venta, 4:Producto, 5:Precio, 6:Fecha
     */
    private Venta parsearVenta(String[] linea, int numeroLinea, Map<String, Alumno> mapaAlumnos) {
        if (linea == null || linea.length < 7) return null;
        
        // REAJUSTE DE ÍNDICES según el nuevo formato CSV
        String cif = obtenerValor(linea, 0);
        String nombre = obtenerValor(linea, 1);
        String email = obtenerValor(linea, 2);
        String idStr = obtenerValor(linea, 3);
        String nombreProducto = obtenerValor(linea, 4);
        String precioStr = obtenerValor(linea, 5);
        String fechaStr = obtenerValor(linea, 6);
        
        if (idStr.isEmpty() || cif.isEmpty()) return null;
        
        try {
            Long idVenta = Long.valueOf(idStr);
            double precio = parsearDouble(precioStr, numeroLinea);
            LocalDate fecha = parsearFecha(fechaStr, numeroLinea);
            
            // 1. Unificación de Alumno
            Alumno alumnoAsociado = mapaAlumnos.computeIfAbsent(cif, k -> 
                new Alumno(0L, k, nombre, email)
            );
            
            // 2. CREACIÓN DEL OBJETO PRODUCTO (Soluciona el error de compilación)
            Producto productoObj = new Producto(nombreProducto);
            
            // 3. Retorno de la Venta con el nuevo constructor
            return new Venta(idVenta, alumnoAsociado, productoObj, precio, fecha);

        } catch (NumberFormatException e) {
            System.err.println("Línea " + numeroLinea + ": Formato ID inválido.");
            return null;
        }
    }

    private String obtenerValor(String[] linea, int index) {
        return (index >= 0 && index < linea.length && linea[index] != null) ? linea[index].trim() : "";
    }

    private double parsearDouble(String valor, int numeroLinea) {
        if (valor.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(valor.replaceAll("[^0-9.,\\-]", "").replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private LocalDate parsearFecha(String valor, int numeroLinea) {
        if (valor.isEmpty()) return LocalDate.now();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(valor, formatter);
            } catch (DateTimeException e) {}
        }
        return LocalDate.now();
    }
}