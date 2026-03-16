package controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.RegistroCSV;
import services.DatosCSVService;

/**
 * The Class VentanaAniadirDatosController.
 */
public class VentanaAniadirDatosController {

    /** The btn cerrar. */
    @FXML
    private Button btn_Cerrar;

    /** The btn aniadir. */
    @FXML
    private Button btn_aniadir;

    /** The btn producto. */
    @FXML
    private MenuButton btn_producto;

    /** The dp fecha. */
    @FXML
    private DatePicker dp_fecha;

    /** The lbl precio. */
    @FXML
    private Label lbl_precio;

    /** The tf cif. */
    @FXML
    private TextField tf_Cif;

    /** The tf email. */
    @FXML
    private TextField tf_Email;

    /** The tf ID. */
    @FXML
    private TextField tf_ID;

    /** The tf nombre. */
    @FXML
    private TextField tf_Nombre;

    /**
     * Maneja el evento de clic sobre el botón "Añadir"
     * <p>
     * Al activarse, comprueba que todos los campos de la vista esté cumplimentados
     * para después añadir los datos.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento asociado
     */
    @FXML
    void agregarDato(ActionEvent event) {

        if (!camposValidos()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText(null);
            alert.setContentText("Debe completar todos los campos antes de añadir.");
            alert.showAndWait();
            return;
        }

        // Parsear precio
        double precio;
        try {
            precio = Double.parseDouble(
                    lbl_precio.getText().replace("€", "").replace(",", ".").trim()
            );
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Precio inválido");
            alert.setHeaderText(null);
            alert.setContentText("El precio no es válido.");
            alert.showAndWait();
            return;
        }

        // Crear registro
        RegistroCSV nuevoRegistro = new RegistroCSV(
                tf_ID.getText().trim(),
                tf_Nombre.getText().trim(),
                tf_Cif.getText().trim(),
                tf_Email.getText().trim(),
                btn_producto.getText().trim(),
                precio,
                dp_fecha.getValue()
        );

        try {
            // Añadir al CSV temporal
            DatosCSVService.getInstance().añadirRegistro(nuevoRegistro);

            // Limpiar campos para nuevo ingreso
            limpiarCampos();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al añadir");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo añadir el registro al CSV temporal.");
            alert.showAndWait();
        }
    }

    /**
     * Realiza una comprobación de que todos los campos del formulario estén debidamente cumplimentados.
     *
     * @return true, si todos los elementos se encuentran completos
     */

    private boolean camposValidos() {
        return !tf_ID.getText().isBlank()
                && !tf_Nombre.getText().isBlank()
                && !tf_Cif.getText().isBlank()
                && !tf_Email.getText().isBlank()
                && btn_producto.getText() != null && !btn_producto.getText().isBlank()
                && !lbl_precio.getText().isBlank()
                && dp_fecha.getValue() != null;
    }

    /**
     *  Gestiona que los cambos una vez añadidos, vuelvan a estar vacíos para introducir más si fuera necesario.
     */

    private void limpiarCampos() {
        tf_ID.clear();
        tf_Nombre.clear();
        tf_Cif.clear();
        tf_Email.clear();
        btn_producto.setText("Producto"); // texto por defecto
        lbl_precio.setText("0,0€");
        dp_fecha.setValue(null);
    }

    /**
     * Gestiona el evento que permite cerrar la ventana cuando ya no se requiera su uso.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento asociado
     */

    @FXML
    void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) btn_Cerrar.getScene().getWindow();
        stage.close();
    }

    /**
     * Maneja el evento de clic sobre el menú desplegable "Productos"
     * <p>
     * Despliega las opciones a elegir, estableciendo el precio ya definido.
     *
     * @param event evento de ratón generado al hacer clic sobre el elemento asociado
     */

    @FXML
    private void seleccionarProducto(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        btn_producto.setText(item.getText());

        switch(item.getText()) {
            case "Curso Básico" -> lbl_precio.setText("295");
            case "Curso Intermedio" -> lbl_precio.setText("495");
            case "Curso Avanzado" -> lbl_precio.setText("895");
        }
    }
}