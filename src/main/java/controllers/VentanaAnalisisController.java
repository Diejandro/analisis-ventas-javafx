package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Alumno;
import models.RegistroCSV;
import models.Venta;
import services.DatosCSVService;

/**
 * The Class VentanaAnalisisController.
 */
public class VentanaAnalisisController {

    /** The root pane general. */
    @FXML
    private BorderPane rootPaneGeneral;

    /** The loader general. */
    // Controladores y roots de cada vista
    private FXMLLoader loaderGeneral;
    
    /** The anchor general. */
    private AnchorPane anchorGeneral;
    
    /** The controller general. */
    private VentanaAnalisisGeneralController controllerGeneral;

    /** The loader grafica. */
    private FXMLLoader loaderGrafica;
    
    /** The anchor graficas. */
    private AnchorPane anchorGraficas;
    
    /** The controller grafica. */
    private VentanaAnalisisGraficaController controllerGrafica;

    /** The loader HTML. */
    private FXMLLoader loaderHTML;
    
    /** The root HTML. */
    private VBox rootHTML;
    
    /** The controller HTML. */
    //private VentanaAnalisisHTMLController controllerHTML;
    
    /** The controller Informe. */
    private VentanaAnalisisInformeController controllerInforme;

    /**
     * Inicializa el controlador y carga todas las vistas de análisis.
     * <p>
     * Durante la inicialización se cargan las distintas vistas FXML
     * (general, gráfica y HTML), se obtienen sus controladores asociados
     * y se establece la vista general como contenido visible por defecto.
     * De este modo, los datos permanecen sincronizados entre todas las vistas.
     */
    @FXML
    public void initialize() {
        try {
            // Cargar Ventana Analisis General
            loaderGeneral = new FXMLLoader(getClass().getResource("/fxml/VentanaAnalisisGeneral.fxml"));
            anchorGeneral = loaderGeneral.load();
            controllerGeneral = loaderGeneral.getController();

            // Cargar Ventana Analisis Gráfica
            loaderGrafica = new FXMLLoader(getClass().getResource("/fxml/VentanaAnalisisGrafica.fxml"));
            anchorGraficas = loaderGrafica.load();
            controllerGrafica = loaderGrafica.getController();

            // Cargar Ventana Analisis HTML
            loaderHTML = new FXMLLoader(getClass().getResource("/fxml/VentanaAnalisisInforme.fxml"));
            rootHTML = loaderHTML.load();
            controllerInforme = loaderHTML.getController();

            // Mostrar vista general por defecto
            rootPaneGeneral.setCenter(anchorGeneral);

            // Actualizar el Label si ya hay datos cargados
            controllerGeneral.actualizarTotales();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convierte la lista de RegistroCSV a List<Venta>.
     *
     * @return the list
     */
    private List<Venta> obtenerVentas() {
        ObservableList<RegistroCSV> registros = DatosCSVService.getInstance().getDatos();
        return registros.stream()
                .map(r -> {
                	Alumno alumno = new Alumno(0L, r.getCif(), r.getNombre(), r.getEmail());
                	
                	return new Venta(
                			Long.valueOf(r.getId()),
                			alumno,
                			r.getProducto(),
                			r.getPrecio(),
                			r.getFecha()
                			
                );})
                .collect(Collectors.toList());
    }

    /**
     * Maneja el evento de clic sobre la opción "General".
     * <p>
     * Al activarse, establece la vista general en el panel central del
     * contenedor principal y solicita al controlador general la
     * actualización de los totales mostrados en la interfaz.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento asociado
     */
    @FXML
    void onClickedGeneral(MouseEvent event) {
        rootPaneGeneral.setCenter(anchorGeneral);
        controllerGeneral.actualizarTotales();
    }

    /**
     * Maneja el evento del clic sobre la opción "Gráficos".
     * <p>
     * Al activase, establece la vista gráficos en el panel central del
     * contenedor principal y solicita al controlador gráficas la 
     * represetaicón gráfica de los datos cargados.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento solicitado
     */
    @FXML
    void onClickedGraphics(MouseEvent event) {
        rootPaneGeneral.setCenter(anchorGraficas);

        // Convertir RegistroCSV → Venta al cargar
        List<Venta> ventas = obtenerVentas();
        if (ventas != null && !ventas.isEmpty()) {
            controllerGrafica.cargarDatos(ventas);
        }
    }

    /**
     * Maneja el evento de clic sobre la opción para un "informe HTML".
     * <p>
     * Al activarse, establece la vista del informe HTML en el panel central del
     * contenedor principal y solicita al controlador del informeHMTL la creación
     * de un informe HTML con los datos cargados.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento solicitado
     */
    @FXML
    void onClickedReport(MouseEvent event) {
        rootPaneGeneral.setCenter(rootHTML);

        List<Venta> ventas = obtenerVentas();
        if (ventas != null && !ventas.isEmpty()) {
            controllerInforme.actualizarContenido(ventas);
        }
    }

    /**
     * Maneja el evento de clic sobre la opción "agregar datos".
     * <p>
     * Al activarse, abre una vista secundaria la cual se encarga de 
     * gestionar la adición de datos al documento cargado.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento solicitado
     */
    @FXML
    void onClickedData(MouseEvent event) {
        abrirVentanaAgregarDatos();
    }

    /**
     * Abre la ventana de agregación de datos.
     * <p>
     * Carga la vista FXML correspondiente, crea una nueva ventana modal
     * y bloquea la interacción con la ventana principal hasta que
     * la ventana secundaria sea cerrada.
     */
    private void abrirVentanaAgregarDatos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentanaAniadirDatos.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Agregar datos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el archivo ya sea ".csv" o ".txt".
     * <p>
     * Abre un selector de archivos del sistema para elegir un archivo compatible.
     * El archivo seleccionado es procesado y sus datos se cargan en la aplicación,
     * actualizando las vistas actualmente visibles en función de su estado.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento solicitado
     */
    @FXML
    void onClickedLoadCSV(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV o TXT", "*.csv", "*.txt"));

        File file = fileChooser.showOpenDialog(rootPaneGeneral.getScene().getWindow());
        if (file != null) {
            try {
                DatosCSVService.getInstance().cargarCSV(file);

                List<Venta> ventas = obtenerVentas();

                // Actualizar las vistas que ya están abiertas
                if (rootPaneGeneral.getCenter() == anchorGeneral) {
                    controllerGeneral.actualizarTotales();
                }
                if (rootPaneGeneral.getCenter() == anchorGraficas) {
                    controllerGrafica.cargarDatos(ventas);
                }
                if (rootPaneGeneral.getCenter() == rootHTML) {
                    controllerInforme.actualizarContenido(ventas);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
