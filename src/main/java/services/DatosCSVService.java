package services;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Alumno;
import models.RegistroCSV;
import models.Venta;
import models.Producto;

/**
 * Servicio central para la gestión de datos. 
 * Maneja archivos temporales, persistencia y transformación de modelos.
 */
public class DatosCSVService {

    private static DatosCSVService instance;
    private File archivoTemporal;
    private char separador;
    
    // Flag de control para el cierre de la aplicación
    private boolean cambiosSinGuardar = false;

    // Lista que alimenta las tablas de la interfaz
    private final ObservableList<RegistroCSV> datos = FXCollections.observableArrayList();

    private DatosCSVService() {}

    public static DatosCSVService getInstance() {
        if (instance == null) {
            instance = new DatosCSVService();
        }
        return instance;
    }

    /* ============================================================
       MÉTODOS DE ESTADO (Cruciales para el Main)
       ============================================================ */

    /**
     * Devuelve true si hay datos nuevos que no se han guardado en un archivo definitivo.
     */
    public boolean hayCambiosSinGuardar() {
        return cambiosSinGuardar;
    }

    /**
     * Restablece el flag de cambios. Se usa tras guardar con éxito.
     */
    public void marcarComoGuardado() {
        this.cambiosSinGuardar = false;
    }

    /* ============================================================
       GESTIÓN DE ARCHIVOS
       ============================================================ */

    public void cargarCSV(File archivo) throws IOException {
        this.separador = detectarSeparador(archivo);
        this.archivoTemporal = crearTemporal(archivo);

        datos.clear();
        leerCSV(archivoTemporal);
        this.cambiosSinGuardar = false;
    }

    private void leerCSV(File archivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esCabecera = true;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                // Salto de cabecera inteligente
                if (esCabecera && (linea.toUpperCase().contains("CIF") || linea.toUpperCase().contains("PRODUCTO"))) {
                    esCabecera = false;
                    continue;
                }
                esCabecera = false;

                String[] campos = linea.split(String.valueOf(separador));
                RegistroCSV registro = RegistroCSV.fromArray(campos);
                if (registro != null) datos.add(registro);
            }
        }
    }

    public void añadirRegistro(RegistroCSV registro) throws IOException {
        if (registro != null) {
            datos.add(registro);
            escribirLineaTemporal(registro);
            this.cambiosSinGuardar = true; // Marcamos que hay trabajo sin persistir
        }
    }

    private void escribirLineaTemporal(RegistroCSV r) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemporal, true))) {
            bw.newLine();
            bw.write(r.toCSV(separador));
        }
    }

    public void guardarComo(File destino) throws IOException {
        if (archivoTemporal != null && archivoTemporal.exists()) {
            Files.copy(archivoTemporal.toPath(), destino.toPath(), 
                       java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            this.cambiosSinGuardar = false;
        }
    }

    /* ============================================================
       TRANSFORMACIÓN Y UTILIDADES
       ============================================================ */

    /**
     * Convierte los registros planos en objetos Venta con lógica de negocio.
     */
    public List<Venta> obtenerVentas() {
        Map<String, Alumno> alumnosMap = new HashMap<>();
        
        return datos.stream()
                .filter(r -> r != null && r.getId() != null)
                .map(r -> {
                    Alumno alumnoUnificado = alumnosMap.computeIfAbsent(r.getCif().trim(), cifLimpio ->
                        new Alumno(0L, cifLimpio, r.getNombre().trim(), r.getEmail().trim())
                    );
                    
                    // r.getProducto() ya devuelve un Record Producto
                    return new Venta( 
                            Long.valueOf(r.getId()), 
                            alumnoUnificado,
                            r.getProducto(), 
                            r.getPrecio(),
                            r.getFecha()
                    );
                })
                .collect(Collectors.toList());
    }

    private File crearTemporal(File original) throws IOException {
        File temp = File.createTempFile("app_ventas_temp_", ".csv");
        Files.copy(original.toPath(), temp.toPath(), 
                   java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return temp;
    }

    private char detectarSeparador(File archivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine();
            if (linea != null && linea.contains(";")) return ';';
            return ',';
        } catch (Exception e) {
            return ';';
        }
    }

    public void limpiarTemporal() {
        if (archivoTemporal != null && archivoTemporal.exists()) {
            archivoTemporal.deleteOnExit();
        }
    }

    // Getters para la interfaz
    public ObservableList<RegistroCSV> getDatos() { return datos; }
    public char getSeparador() { return separador; }
    public File getArchivoTemporal() { return archivoTemporal; }
}