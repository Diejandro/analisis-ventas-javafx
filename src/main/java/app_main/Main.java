package app_main;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import services.DatosCSVService;

/**
 * Clase principal de la aplicación ESL.
 * Gestiona el ciclo de vida de la ventana principal y la persistencia al cerrar.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Carga de la vista principal
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/VentanaAnalisis.fxml")
        );
        Parent root = loader.load();

        // Configuración de la escena
        primaryStage.setTitle("ESL (English School Learning)");
        primaryStage.setScene(new Scene(root));
        
        // Dimensiones mínimas según diseño
        primaryStage.setMinWidth(1315);
        primaryStage.setMinHeight(810);
        primaryStage.setResizable(true);

        /**
         * Gestión del cierre de la aplicación.
         * Verifica si hay datos en el temporal que deban persistirse en un CSV real.
         */
        primaryStage.setOnCloseRequest(event -> {
            DatosCSVService service = DatosCSVService.getInstance();

            // 1. Comprobar si hay cambios en el archivo temporal sin guardar en disco
            if (service.hayCambiosSinGuardar()) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cambios sin guardar");
                alert.setHeaderText("Existen datos añadidos que no se han guardado permanentemente");
                alert.setContentText("¿Desea guardarlos en un archivo CSV antes de salir?");

                ButtonType btnGuardar = new ButtonType("Guardar");
                ButtonType btnSalir = new ButtonType("Salir sin guardar");
                ButtonType btnCancelar = ButtonType.CANCEL;

                alert.getButtonTypes().setAll(btnGuardar, btnSalir, btnCancelar);

                Optional<ButtonType> resultado = alert.showAndWait();

                if (resultado.isPresent()) {
                    if (resultado.get() == btnGuardar) {
                        FileChooser chooser = new FileChooser();
                        chooser.setTitle("Guardar archivo CSV final");
                        chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
                        );

                        File destino = chooser.showSaveDialog(primaryStage);

                        if (destino != null) {
                            try {
                                service.guardarComo(destino);
                                service.marcarComoGuardado();
                            } catch (IOException e) {
                                mostrarErrorCierre("No se pudo guardar el archivo: " + e.getMessage());
                                event.consume(); // Bloquea el cierre si hay error
                                return;
                            }
                        } else {
                            // Si el usuario cancela el FileChooser, no cerramos la app
                            event.consume();
                            return;
                        }

                    } else if (resultado.get() == btnCancelar) {
                        // Si el usuario pulsa cancelar en el diálogo, no cerramos
                        event.consume();
                        return;
                    }
                }
            }

            // 2. Limpieza del archivo temporal del sistema antes de salir definitivamente
            service.limpiarTemporal();
        });

        primaryStage.show();
    }

    /**
     * Utilidad para mostrar errores durante el proceso de cierre.
     */
    private void mostrarErrorCierre(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al salir");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}