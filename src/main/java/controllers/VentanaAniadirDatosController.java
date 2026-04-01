package controllers;

import java.io.IOException;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Producto;
import models.RegistroCSV;
import services.DatosCSVService;

/**
 * Controlador para la ventana de inserción de nuevos registros.
 * Gestiona la validación de campos y la persistencia temporal de la nueva venta.
 */
public class VentanaAniadirDatosController {

    @FXML private Button btn_Cerrar;
    @FXML private Button btn_aniadir;
    @FXML private MenuButton btn_producto;
    @FXML private DatePicker dp_fecha;
    @FXML private Label lbl_precio;
    @FXML private TextField tf_Cif;
    @FXML private TextField tf_Email;
    @FXML private TextField tf_ID;
    @FXML private TextField tf_Nombre;

    /**
     * Maneja la acción de añadir un nuevo registro.
     * Valida los campos, crea los objetos necesarios y los guarda a través del servicio.
     */
    @FXML
    void agregarDato(ActionEvent event) {

        if (!camposValidos()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", 
                         "Debe completar todos los campos antes de añadir.");
            return;
        }

        // 1. Procesar el precio desde el Label
        double precio;
        try {
            String textoPrecio = lbl_precio.getText()
                    .replace("€", "")
                    .replace(",", ".")
                    .trim();
            precio = Double.parseDouble(textoPrecio);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Precio inválido", 
                         "El formato del precio no es correcto.");
            return;
        }

        // 2. Crear el objeto Producto (Basado en el Record)
        // Esto es lo que permite la extendibilidad que buscamos
        Producto productoSeleccionado = new Producto(btn_producto.getText().trim());

        // 3. Crear el RegistroCSV con el nuevo objeto Producto
        RegistroCSV nuevoRegistro = new RegistroCSV(
                tf_ID.getText().trim(),
                tf_Nombre.getText().trim(),
                tf_Cif.getText().trim(),
                tf_Email.getText().trim(),
                productoSeleccionado, // Pasamos el objeto, no el String
                precio,
                dp_fecha.getValue()
        );

        try {
            // Guardar en el servicio (CSV temporal)
            DatosCSVService.getInstance().añadirRegistro(nuevoRegistro);

            // Éxito y limpieza
            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro añadido", 
                         "La venta se ha registrado correctamente.");
            limpiarCampos();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", 
                         "No se pudo escribir en el archivo temporal.");
        }
    }

    /**
     * Verifica que no haya campos vacíos y que se haya seleccionado un producto.
     */
    private boolean camposValidos() {
        return !tf_ID.getText().isBlank()
                && !tf_Nombre.getText().isBlank()
                && !tf_Cif.getText().isBlank()
                && !tf_Email.getText().isBlank()
                && btn_producto.getText() != null 
                && !btn_producto.getText().equals("Producto") // Evita el texto por defecto
                && !lbl_precio.getText().isBlank()
                && dp_fecha.getValue() != null;
    }

    /**
     * Restablece el formulario a su estado inicial.
     */
    private void limpiarCampos() {
        tf_ID.clear();
        tf_Nombre.clear();
        tf_Cif.clear();
        tf_Email.clear();
        btn_producto.setText("Producto");
        lbl_precio.setText("0,0€");
        dp_fecha.setValue(LocalDate.now()); // Por defecto la fecha de hoy
    }

    @FXML
    void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) btn_Cerrar.getScene().getWindow();
        stage.close();
    }

    /**
     * Actualiza el texto del botón y el label de precio según la selección del usuario.
     */
    @FXML
    private void seleccionarProducto(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        String seleccion = item.getText();
        btn_producto.setText(seleccion);

        // Precios fijos según el catálogo
        switch(seleccion) {
            case "Curso Básico" -> lbl_precio.setText("295");
            case "Curso Intermedio" -> lbl_precio.setText("495");
            case "Curso Avanzado" -> lbl_precio.setText("895");
            default -> lbl_precio.setText("0,0");
        }
    }

    /**
     * Utilidad interna para centralizar la creación de alertas.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}