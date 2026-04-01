package controllers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import models.ClienteEstadistica;
import models.Venta;
import services.AnalizadorVentas;
import services.GeneradorInformeHTML;

/**
 * Controlador para la vista de informe.
 * Muestra el informe en formato texto plano y gestiona la exportación a HTML.
 */
public class VentanaAnalisisInformeController {

    @FXML private Button b_download;
    @FXML private TextArea tField_infome;

    private List<Venta> ventasActuales;
    private GeneradorInformeHTML generador;
    
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
    private static final DecimalFormat dfNumber = new DecimalFormat("#,##0.00", symbols);

    @FXML
    public void initialize() {
        if (tField_infome != null) {
            tField_infome.setEditable(false);
            tField_infome.setWrapText(true);
            // Aplicamos fuente monoespaciada para que las columnas de texto no se desalineen
            tField_infome.setStyle("-fx-font-family: 'Consolas', 'Monospaced', 'Courier New'; -fx-font-size: 13px;");
        }
    }

    /**
     * Punto de entrada principal para actualizar la vista con nuevos datos.
     */
    public void actualizarContenido(List<Venta> datos) {
        if (datos == null || datos.isEmpty()) {
            mostrarMensajeVacio();
            return;
        }

        this.ventasActuales = datos;
        this.generador = new GeneradorInformeHTML(datos);

        try {
            String textoInforme = generarInformeTextoPlano(datos);
            tField_infome.setText(textoInforme);
        } catch (Exception e) {
            tField_infome.setText("Error crítico al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Construye la cadena de texto con formato de tabla para el TextArea.
     */
    private String generarInformeTextoPlano(List<Venta> datos) {
        StringBuilder texto = new StringBuilder();
        AnalizadorVentas av = new AnalizadorVentas(datos);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        texto.append("═══════════════════════════════════════════════════════════════\n");
        texto.append("          INFORME DE VENTAS - CURSOS DE INGLÉS\n");
        texto.append("═══════════════════════════════════════════════════════════════\n\n");

        // KPIs
        int totalVentas = av.obtenerTotalVentas();
        double ingresosTotales = av.total("Precio");
        double precioMedio = totalVentas > 0 ? ingresosTotales / totalVentas : 0;

        texto.append("📊 RESUMEN GENERAL\n");
        texto.append("───────────────────────────────────────────────────────────────\n");
        texto.append(String.format("  Total ventas:        %d\n", totalVentas));
        texto.append(String.format("  Ingresos totales:    %s €\n", dfNumber.format(ingresosTotales)));
        texto.append(String.format("  Precio medio:        %s €\n", dfNumber.format(precioMedio)));
        texto.append(String.format("  Clientes únicos:     %d\n", av.obtenerTotalClientes()));
        texto.append("\n");

        // Agrupación por productos (Usando los nombres definidos en el CSV)
        texto.append("📦 VENTAS POR PRODUCTO\n");
        texto.append("───────────────────────────────────────────────────────────────\n");
        String[] nombresProductos = {"Curso básico", "Curso intermedio", "Curso avanzado"};
        for (String nombre : nombresProductos) {
            int cantidad = av.flujoVentasPorProducto(nombre);
            double facturacion = av.flujoFacturacionProducto(nombre);
            texto.append(String.format("  %-20s  %3d ventas  →  %10s €\n", 
                                      nombre, cantidad, dfNumber.format(facturacion)));
        }
        texto.append("\n");

        // Top 10 Clientes (Acceso a Record)
        List<ClienteEstadistica> topClientes = av.topClientes(10);
        texto.append("🏆 TOP 10 CLIENTES (por volumen de compra)\n");
        texto.append("───────────────────────────────────────────────────────────────\n");
        
        int posicion = 1;
        for (ClienteEstadistica c : topClientes) {
            texto.append(String.format("%2d. %-25s  %3d compras\n", 
                                      posicion++, c.nombre(), c.totalCompras()));
            texto.append(String.format("    Email: %s\n", c.email()));
        }
        texto.append("\n");

        // Transacciones individuales
        texto.append("📋 LISTADO COMPLETO DE VENTAS\n");
        texto.append("═══════════════════════════════════════════════════════════════\n");
        texto.append(String.format("%-5s %-25s %-15s %-20s\n", "ID", "Producto", "Precio", "Fecha"));
        texto.append("───────────────────────────────────────────────────────────────\n");

        for (Venta v : datos) {
            texto.append(String.format("%-5s %-25s %10s €  %s\n",
                                      v.getId(),
                                      v.getProducto().nombre(), // Acceso al Record Producto
                                      dfNumber.format(v.getPrecio()),
                                      v.getFecha().format(df)));
        }
        
        texto.append("\n═══════════════════════════════════════════════════════════════\n");
        return texto.toString();
    }

    @FXML
    void onClickedDownload(MouseEvent event) {
        if (ventasActuales == null || ventasActuales.isEmpty()) {
            mostrarAdvertencia("No hay datos cargados para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar informe a HTML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Web Page", "*.html"));
        fileChooser.setInitialFileName("Informe_Ventas.html");

        File archivoDestino = fileChooser.showSaveDialog(tField_infome.getScene().getWindow());

        if (archivoDestino != null) {
            try {
                generador.generarInforme(archivoDestino.getAbsolutePath());
                mostrarExito("Exportación completada", "El informe se ha guardado en:\n" + archivoDestino.getName());
            } catch (IOException e) {
                mostrarError("No se pudo escribir el archivo: " + e.getMessage());
            }
        }
    }

    /* ===============================
       MÉTODOS PRIVADOS Y UTILIDADES
       =============================== */

    private void mostrarMensajeVacio() {
        tField_infome.setText("═══════════════════════════════════════════════════════════════\n"
                            + "                    📊 SIN DATOS CARGADOS\n"
                            + "═══════════════════════════════════════════════════════════════\n\n"
                            + "  Por favor, carga un archivo CSV válido para generar el informe.\n");
    }

    public void limpiar() {
        ventasActuales = null;
        generador = null;
        mostrarMensajeVacio();
    }

    public void refrescar() {
        if (ventasActuales != null) actualizarContenido(ventasActuales);
    }

    private void mostrarError(String msj) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText(msj);
        a.showAndWait();
    }

    private void mostrarAdvertencia(String msj) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Atención");
        a.setContentText(msj);
        a.showAndWait();
    }

    private void mostrarExito(String titulo, String msj) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(titulo);
        a.setContentText(msj);
        a.showAndWait();
    }
}