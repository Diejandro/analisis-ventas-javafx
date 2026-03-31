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
import models.Venta;

// TODO: Auto-generated Javadoc
/**
 * Lector de archivos CSV con detección automática de separador (, o ;).
 */
public class LectorCSV {
    
    /** The Constant DATE_FORMATTERS. */
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ISO_LOCAL_DATE
    };

    /**
     * Lee un archivo CSV detectando automáticamente el separador.
     *
     * @param archivo Archivo CSV a leer
     * @return Lista de ventas
     * @throws IOException Si hay error al leer el archivo
     */
    public List<Venta> leerCSV(File archivo) throws IOException {
        if (archivo == null || !archivo.exists()) {
            throw new IllegalArgumentException("El archivo no existe o es nulo");
        }

        // Detectar el separador del CSV
        char separador = detectarSeparador(archivo);
        System.out.println("Separador detectado: '" + separador + "'");

        List<Venta> ventas = new ArrayList<>();
        
        try (FileReader fileReader = new FileReader(archivo);
             CSVReader csvReader = crearCSVReader(fileReader, separador)) {
            
            List<String[]> registros = csvReader.readAll();
            System.out.println("Leyendo " + registros.size() + " líneas de CSV...");
            
            Map<String, Alumno> mapaAlumnos = new HashMap<>();
            
            
            for (int i = 0; i < registros.size(); i++) {
                String[] linea = registros.get(i);
                try {
                    Venta venta = parsearVenta(linea, i + 2, mapaAlumnos); // +2 porque salta cabecera y el índice 0
                    if (venta != null) {
                        ventas.add(venta);
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar la línea " + (i + 2) + ": " + e.getMessage());
                }
            }
        } catch (CsvException e) {
            throw new IOException("Error al parsear el archivo CSV: " + e.getMessage(), e);
        }
        
        System.out.println("Total de ventas cargadas exitosamente: " + ventas.size());
        return ventas;
    }

    /**
     * Detecta automáticamente el separador del CSV (coma o punto y coma).
     *
     * @param archivo Archivo CSV
     * @return Carácter separador detectado
     * @throws IOException Si hay error al leer el archivo
     */
    private char detectarSeparador(File archivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            // Leer la primera línea (cabecera)
            String primeraLinea = reader.readLine();
            
            if (primeraLinea == null || primeraLinea.isEmpty()) {
                System.out.println("Archivo vacío, usando separador por defecto: ','");
                return ',';
            }

            // Contar ocurrencias de cada posible separador
            int contadorComas = contarOcurrencias(primeraLinea, ',');
            int contadorPuntoYComa = contarOcurrencias(primeraLinea, ';');

            System.out.println("Detección: " + contadorComas + " comas, " + contadorPuntoYComa + " punto y coma");

            // El separador es el que tenga más ocurrencias
            // Si hay al menos 6 separadores (7 campos - 1), es un separador válido
            if (contadorPuntoYComa >= 6) {
                return ';';
            } else if (contadorComas >= 6) {
                return ',';
            } else {
                // Por defecto, usar coma
                System.out.println("No se detectó separador claro, usando ','");
                return ',';
            }
        }
    }

    /**
     * Cuenta las ocurrencias de un carácter en una cadena.
     *
     * @param texto Texto donde buscar
     * @param caracter Carácter a contar
     * @return Número de ocurrencias
     */
    private int contarOcurrencias(String texto, char caracter) {
        int contador = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == caracter) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Crea un CSVReader configurado con el separador especificado.
     *
     * @param fileReader FileReader del archivo
     * @param separador Carácter separador
     * @return CSVReader configurado
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private CSVReader crearCSVReader(FileReader fileReader, char separador) throws IOException {
        
        BufferedReader br = new BufferedReader(fileReader);
        br.mark(1500);

        String firstLine = br.readLine();

        boolean tieneEncabezado = false;

        if (firstLine != null) {
            String lower = firstLine.toLowerCase();
            // Detectamos encabezado por palabras clave comunes
            tieneEncabezado = 
                lower.contains("id") &&
                lower.contains("nombre") &&
                lower.contains("cif") &&
                lower.contains("email") &&
                lower.contains("producto") &&
                lower.contains("precio") &&
                lower.contains("fecha");
        }

        // Si NO es encabezado, volver al inicio
        if (!tieneEncabezado) {
            br.reset();
        }

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separador)
                .build();

        return new CSVReaderBuilder(br)
                .withSkipLines(tieneEncabezado ? 1 : 0)
                .withCSVParser(parser)
                .build();
    }


    /**
     * Parsea una línea del CSV y crea un objeto Venta.
     *
     * @param linea Array con los campos de la línea
     * @param numeroLinea Número de línea para mensajes de error
     * @return Objeto Venta o null si hay error
     */
    private Venta parsearVenta(String[] linea, int numeroLinea, Map<String, Alumno> mapaAlumnos) {
        if (linea == null || linea.length < 7) {
            System.err.println("Línea " + numeroLinea + " incompleta. Esperando 7 campos, encontrado: "
                    + (linea != null ? linea.length : 0));
            return null;
        }
        
        String idStr = obtenerValor(linea, 0);
        String nombre = obtenerValor(linea, 1).trim();
        String cif = obtenerValor(linea, 2).trim();
        String email = obtenerValor(linea, 3).trim();
        String producto = obtenerValor(linea, 4);
        String precioStr = obtenerValor(linea, 5);
        String fechaStr = obtenerValor(linea, 6);
        
        // Validar ID (campo obligatorio)
        if (idStr.isEmpty()) {
            System.err.println("Línea " + numeroLinea + ": ID vacío, se omite esta línea");
            return null;
        }
        
        try {
        	Long idVenta = Long.valueOf(idStr.trim());
        	
        	double precio = parsearDouble(precioStr, numeroLinea);
        	LocalDate fecha = parsearFecha(fechaStr, numeroLinea);
        	
        	Alumno alumnoAsociado = mapaAlumnos.computeIfAbsent(cif, k -> new Alumno(0L, k, nombre, email));
        	
        	return new Venta(idVenta, alumnoAsociado, producto, precio, fecha);
        }catch(NumberFormatException e) {
        	System.err.println("Línea " + numeroLinea + ": Error de formato en el ID '" + idStr + "'");
            return null;
        }
    }

    /**
     * Obtiene el valor de una posición del array.
     *
     * @param linea Array de campos
     * @param index Índice del campo
     * @return Valor del campo o cadena vacía
     */
    private String obtenerValor(String[] linea, int index) {
        if (index >= 0 && index < linea.length && linea[index] != null) {
            return linea[index].trim();
        }
        return "";
    }

    /**
     * Parsea un String a double.
     *
     * @param valor String a parsear
     * @param numeroLinea Número de línea para mensajes de error
     * @return Valor double o 0.0 si hay error
     */
    private double parsearDouble(String valor, int numeroLinea) {
        if (valor == null || valor.isEmpty()) {
            return 0.0;
        }
        try {
            // Remueve símbolos de moneda y espacios
            valor = valor.replaceAll("[^0-9.,\\-]", "");
            // Reemplaza coma por punto si es necesario
            valor = valor.replace(',', '.');
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            System.err.println("Línea " + numeroLinea + ": Error parseando precio '" + valor + "', usando 0.0");
            return 0.0;
        }
    }

    /**
     * Parsea un String a LocalDate probando múltiples formatos.
     *
     * @param valor String a parsear
     * @param numeroLinea Número de línea para mensajes de error
     * @return LocalDate o fecha actual si hay error
     */
    private LocalDate parsearFecha(String valor, int numeroLinea) {
        if (valor == null || valor.isEmpty()) {
            System.err.println("Línea " + numeroLinea + ": fecha vacía, usando fecha actual");
            return LocalDate.now();
        }
        
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(valor.trim(), formatter);
            } catch (DateTimeException e) {
                // Continúa con el siguiente formato
            }
        }
        
        System.err.println("Línea " + numeroLinea + ": Error parseando fecha '" + valor + "', usando fecha actual");
        return LocalDate.now();
    }
}