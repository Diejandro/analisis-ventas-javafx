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
 * Controlador para la vista de informe
 * Muestra el informe en formato texto plano en un TextArea
 */
public class VentanaAnalisisInformeController {

    @FXML
    private Button b_download;

    @FXML
    private TextArea tField_infome;

    private List<Venta> ventasActuales;
    private GeneradorInformeHTML generador;
    
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
    private static final DecimalFormat dfNumber = new DecimalFormat("#,##0.00", symbols);

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        if (tField_infome != null) {
            tField_infome.setEditable(false);
            tField_infome.setWrapText(true);
        }
    }

    /**
     * Actualiza el contenido del informe en formato texto
     *
     * @param datos Lista de ventas
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
            
            System.out.println("✓ Informe de texto cargado correctamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            tField_infome.setText("Error al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Genera el informe en formato texto plano
     *
     * @param datos Lista de ventas
     * @return String con el informe en texto plano
     */
    private String generarInformeTextoPlano(List<Venta> datos) {
        StringBuilder texto = new StringBuilder();
        AnalizadorVentas av = new AnalizadorVentas(datos);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Título
        texto.append("═══════════════════════════════════════════════════════════════\n");
        texto.append("          INFORME DE VENTAS - CURSOS DE INGLÉS\n");
        texto.append("═══════════════════════════════════════════════════════════════\n\n");

        // Resumen general
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

        // Ventas por producto
        texto.append("📦 VENTAS POR PRODUCTO\n");
        texto.append("───────────────────────────────────────────────────────────────\n");
        String[] productos = {"Curso básico", "Curso intermedio", "Curso avanzado"};
        for (String producto : productos) {
            int cantidad = av.flujoVentasPorProducto(producto);
            double facturacion = av.flujoFacturacionProducto(producto);
            texto.append(String.format("  %-20s  %3d ventas  →  %s €\n", 
                                      producto, cantidad, dfNumber.format(facturacion)));
        }
        texto.append("\n");

        // Top clientes
        List<ClienteEstadistica> topClientes = av.topClientes(10);
        texto.append("🏆 TOP 10 CLIENTES (por número de compras)\n");
        texto.append("───────────────────────────────────────────────────────────────\n");
        
        int posicion = 1;
        for (ClienteEstadistica c : topClientes) {
            texto.append(String.format("%2d. %-25s  %3d compras\n", 
                                      posicion++, c.getNombre(), c.getTotalCompras()));
            texto.append(String.format("    Email: %s\n", c.getEmail()));
        }
        texto.append("\n");

        // Listado completo de ventas
        texto.append("📋 LISTADO COMPLETO DE VENTAS\n");
        texto.append("═══════════════════════════════════════════════════════════════\n");
        texto.append(String.format("%-5s %-25s %-15s %-20s\n", 
                                   "ID", "Producto", "Precio", "Fecha"));
        texto.append("───────────────────────────────────────────────────────────────\n");

        for (Venta v : datos) {
            texto.append(String.format("%-5s %-25s %10s €  %s\n",
                                      v.getId(),
                                      v.getProducto(),
                                      dfNumber.format(v.getPrecio()),
                                      v.getFecha().format(df)));
        }
        
        texto.append("\n");
        texto.append("═══════════════════════════════════════════════════════════════\n");
        texto.append(String.format("Total de registros: %d\n", datos.size()));
        texto.append("═══════════════════════════════════════════════════════════════\n");

        return texto.toString();
    }

    /**
     * Muestra un mensaje cuando no hay datos
     */
    private void mostrarMensajeVacio() {
        tField_infome.setText("═══════════════════════════════════════════════════════════════\n"
                            + "                    📊 NO HAY DATOS\n"
                            + "═══════════════════════════════════════════════════════════════\n\n"
                            + "  Carga un archivo CSV para generar el informe de ventas.\n\n");
    }

    /**
     * Limpia el contenido
     */
    public void limpiar() {
        ventasActuales = null;
        generador = null;
        mostrarMensajeVacio();
    }

    /**
     * Maneja el evento clic sobre el botón "Descargar"
     * Descarga el informe como archivo HTML
     *
     * @param event Evento de ratón
     */
    @FXML
    void onClickedDownload(MouseEvent event) {
        if (ventasActuales == null || ventasActuales.isEmpty()) {
            mostrarAdvertencia("No hay datos para generar el informe");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar informe HTML");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo HTML", "*.html")
        );
        fileChooser.setInitialFileName("informe_ventas.html");

        File archivoDestino = fileChooser.showSaveDialog(tField_infome.getScene().getWindow());

        if (archivoDestino != null) {
            try {
                generador.generarInforme(archivoDestino.getAbsolutePath());
                
                mostrarExito("Informe guardado correctamente", 
                           "El archivo HTML se guardó en:\n" + archivoDestino.getAbsolutePath());
                
                System.out.println("✓ Informe HTML guardado en: " + archivoDestino.getAbsolutePath());
                
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error al guardar el archivo:\n" + e.getMessage());
            }
        }
    }

    /**
     * Refresca el informe con los datos actuales
     */
    public void refrescar() {
        if (ventasActuales != null && !ventasActuales.isEmpty()) {
            actualizarContenido(ventasActuales);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTODOS DE MENSAJES
    // ═══════════════════════════════════════════════════════════

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Atención");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ═══════════════════════════════════════════════════════════
    // GETTERS
    // ═══════════════════════════════════════════════════════════

    public List<Venta> getVentasActuales() {
        return ventasActuales;
    }

    public TextArea getTextArea() {
        return tField_infome;
    }
}