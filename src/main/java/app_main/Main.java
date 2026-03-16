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
 * The Class Main.
 */
public class Main extends Application {

    /**
     * Start.
     *
     * @param primaryStage the primary stage
     * @throws Exception the exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/VentanaAnalisis.fxml")
        );
        Parent root = loader.load();

        primaryStage.setTitle("ESL (English School Learning)");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(1315);
        primaryStage.setMinHeight(810);
        primaryStage.setResizable(true);

        primaryStage.setOnCloseRequest(event -> {

            DatosCSVService service = DatosCSVService.getInstance();

            // 1. Comprobar cambios sin guardar
            if (service.hayCambiosSinGuardar()) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cambios sin guardar");
                alert.setHeaderText("Existen datos añadidos que no se han guardado");
                alert.setContentText("¿Desea guardarlos antes de salir?");

                ButtonType btnGuardar = new ButtonType("Guardar");
                ButtonType btnSalir = new ButtonType("Salir sin guardar");
                ButtonType btnCancelar = ButtonType.CANCEL;

                alert.getButtonTypes().setAll(btnGuardar, btnSalir, btnCancelar);

                Optional<ButtonType> resultado = alert.showAndWait();

                if (resultado.isPresent()) {

                    if (resultado.get() == btnGuardar) {

                        FileChooser chooser = new FileChooser();
                        chooser.setTitle("Guardar archivo CSV");
                        chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("CSV", "*.csv")
                        );

                        File destino = chooser.showSaveDialog(primaryStage);

                        if (destino != null) {
                            try {
                                service.guardarComo(destino);
                                service.marcarComoGuardado();
                            } catch (IOException e) {
                                e.printStackTrace();
                                event.consume();
                                return;
                            }
                        } else {
                            event.consume();
                            return;
                        }

                    } else if (resultado.get() == btnCancelar) {
                        event.consume();
                        return;
                    }
                    // btnSalir → continuar cierre
                }
            }

            // 2. Limpieza segura del archivo temporal
            service.limpiarTemporal();
        });

        primaryStage.show();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

