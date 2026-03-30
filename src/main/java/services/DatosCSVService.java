package services;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Alumno;
import models.RegistroCSV;
import models.Venta;

// TODO: Auto-generated Javadoc
/**
 * The Class DatosCSVService.
 */
public class DatosCSVService {

    /** The instance. */
    private static DatosCSVService instance;

    /** The archivo temporal. */
    private File archivoTemporal;
    
    /** The separador. */
    private char separador;
    
    /** The cambios sin guardar. */
    private boolean cambiosSinGuardar = false;

    /** The datos. */
    private final ObservableList<RegistroCSV> datos = FXCollections.observableArrayList();

    /**
     * Instantiates a new datos CSV service.
     */
    private DatosCSVService() {}

    /**
     * Gets the single instance of DatosCSVService.
     *
     * @return single instance of DatosCSVService
     */
    public static DatosCSVService getInstance() {
        if (instance == null) {
            instance = new DatosCSVService();
        }
        return instance;
    }

    /* ===============================
       CARGA DE CSV
       =============================== */

    /**
     * Cargar CSV.
     *
     * @param archivo the archivo
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void cargarCSV(File archivo) throws IOException {
        this.separador = detectarSeparador(archivo);
        this.archivoTemporal = crearTemporal(archivo);

        datos.clear();
        leerCSV(archivoTemporal);
        cambiosSinGuardar = false;
    }

    /* ===============================
       LECTURA / ESCRITURA
       =============================== */

    /**
     * Leer CSV.
     *
     * @param archivo the archivo
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void leerCSV(File archivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue; // ignoramos líneas vacías
                String[] campos = linea.split(String.valueOf(separador));
                RegistroCSV registro = RegistroCSV.fromArray(campos);
                if (registro != null) datos.add(registro); // solo agregamos si no es null
            }
        }
    }


    /**
     * Añadir registro.
     *
     * @param registro the registro
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void añadirRegistro(RegistroCSV registro) throws IOException {
        datos.add(registro);
        escribirLineaTemporal(registro);
        cambiosSinGuardar = true;
    }

    /**
     * Escribir linea temporal.
     *
     * @param r the r
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void escribirLineaTemporal(RegistroCSV r) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(archivoTemporal, true))) {

            bw.newLine();
            bw.write(r.toCSV(separador));
        }
    }

    /* ===============================
       GUARDADO
       =============================== */

    /**
     * Guardar como.
     *
     * @param destino the destino
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void guardarComo(File destino) throws IOException {
        Files.copy(
            archivoTemporal.toPath(),
            destino.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
        cambiosSinGuardar = false;
    }

    /**
     * Marcar como guardado.
     */
    public void marcarComoGuardado() {
        cambiosSinGuardar = false;
    }

    /* ===============================
       TEMPORAL
       =============================== */

    /**
     * Crear temporal.
     *
     * @param original the original
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private File crearTemporal(File original) throws IOException {
        File temp = File.createTempFile("csv_temp_", ".csv");
        Files.copy(
            original.toPath(),
            temp.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
        return temp;
    }

    /**
     * Limpiar temporal.
     */
    public void limpiarTemporal() {
        if (archivoTemporal != null && archivoTemporal.exists()) {
            archivoTemporal.deleteOnExit();
        }
    }

    /* ===============================
       UTILIDADES
       =============================== */

    /**
     * Detectar separador.
     *
     * @param archivo the archivo
     * @return the char
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private char detectarSeparador(File archivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine();
            if (linea.contains(";")) return ';';
            return ',';
        }
    }

    /**
     * Hay cambios sin guardar.
     *
     * @return true, if successful
     */
    public boolean hayCambiosSinGuardar() {
        return cambiosSinGuardar;
    }

    /**
     * Gets the datos.
     *
     * @return the datos
     */
    public ObservableList<RegistroCSV> getDatos() {
        return datos;
    }

    /**
     * Gets the separador.
     *
     * @return the separador
     */
    public char getSeparador() {
        return separador;
    }

    /**
     * Gets the archivo temporal.
     *
     * @return the archivo temporal
     */
    public File getArchivoTemporal() {
        return archivoTemporal;
    }

    /* ===============================
       NUEVO MÉTODO PARA VENTAS
       =============================== */

 /**
     * Devuelve la lista de Ventas a partir de los registros CSV,
     * filtrando registros nulos o con ID nulo para evitar NullPointerException.
     *
     * @return the list
     */
    public List<Venta> obtenerVentas() {
        return datos.stream()
                .filter(r -> r != null && r.getId() != null)
                .map(r -> {
                	Alumno temp = new Alumno(0L, r.getCif(), r.getNombre(), r.getEmail());
                	
                	return new Venta( 
                			Long.valueOf(r.getId()), 
                			temp,
                			r.getProducto(),
                			r.getPrecio(),
                			r.getFecha()
                			);})
                	.collect(Collectors.toList());
    }


}


