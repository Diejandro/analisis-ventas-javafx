package controllers.graficas;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.Venta;

/**
 * Clase encargada de gestionar y renderizar la tabla de datos (TableView) 
 * utilizando los objetos de dominio Venta.
 */
public class TablaCSV {

    private TableView<Venta> tabla;
    private Pane vPaneTabla;

    /**
     * Constructor que vincula la tabla a un contenedor Pane.
     * * @param vPaneTabla Contenedor donde se insertará la tabla.
     */
    public TablaCSV(Pane vPaneTabla) {
        this.vPaneTabla = vPaneTabla;
        inicializarTabla();
    }

    /**
     * Crea la instancia de TableView y la vincula al tamaño del contenedor.
     */
    private void inicializarTabla() {
        this.tabla = new TableView<>();
        
        // Sincronización de dimensiones con el contenedor FXML
        tabla.prefWidthProperty().bind(vPaneTabla.widthProperty());
        tabla.prefHeightProperty().bind(vPaneTabla.heightProperty());

        // Política de redimensionado manual para permitir el auto-ajuste de columnas
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        vPaneTabla.getChildren().add(tabla);
    }

    /**
     * Carga la lista de ventas en la tabla y genera las columnas dinámicamente.
     * * @param ventas Lista de objetos Venta a mostrar.
     */
    public void cargarDatos(List<Venta> ventas) {
        if (tabla == null) return;
        
        if (ventas == null || ventas.isEmpty()) {
            limpiar();
            return;
        }

        // Limpieza de estados previos
        tabla.getColumns().clear();
        tabla.setItems(FXCollections.observableArrayList(ventas));

        // Configuración de columnas vinculadas a los getters de Venta
        // "id" -> getId(), "nombre" -> getNombre(), etc.
        crearColumna("ID", "id");
        crearColumna("Nombre", "nombre");
        crearColumna("CIF", "cif");
        crearColumna("Email", "email");
        crearColumna("Producto", "producto"); // Renderiza Producto.toString()
        crearColumna("Precio (€)", "precio");
        crearColumna("Fecha", "fecha");

        // Ajuste de ancho automático basado en el contenido real
        tabla.getColumns().forEach(this::autoFitColumn);
    }

    /**
     * Crea una columna y vincula su celda a una propiedad del modelo Venta.
     */
    private void crearColumna(String titulo, String propiedad) {
        TableColumn<Venta, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        
        col.setMinWidth(80);
        tabla.getColumns().add(col);
    }

    /**
     * Calcula el ancho óptimo para la columna basándose en el texto más largo
     * presente en las celdas o en la cabecera.
     */
    private void autoFitColumn(TableColumn<Venta, ?> column) {
        // Medición del texto de la cabecera
        Text tempText = new Text(column.getText());
        double max = tempText.getLayoutBounds().getWidth();

        // Muestreo de las primeras 100 filas para optimizar el rendimiento
        int totalFilas = tabla.getItems().size();
        int filasAMuestrear = Math.min(totalFilas, 100);

        for (int i = 0; i < filasAMuestrear; i++) {
            Object cellData = column.getCellData(i);
            if (cellData != null) {
                tempText = new Text(cellData.toString());
                double width = tempText.getLayoutBounds().getWidth();
                if (width > max) {
                    max = width;
                }
            }
        }

        // Aplicación del ancho con un margen de seguridad (padding)
        column.setPrefWidth(max + 25); 
    }

    /**
     * Vacía los datos de la tabla y elimina las columnas.
     */
    public void limpiar() {
        if (tabla != null) {
            tabla.getItems().clear();
            tabla.getColumns().clear();
        }
    }

    public TableView<Venta> getTabla() {
        return tabla;
    }
}