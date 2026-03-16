package controllers.graficas;

import models.Venta;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Clase encargada de gestionar la tabla CSV.
 */
public class TablaCSV {

    /** The tabla. */
    private TableView<Venta> tabla;
    
    /** The v pane tabla. */
    private Pane vPaneTabla;

    /**
     * Constructor.
     *
     * @param vPaneTabla the v pane tabla
     */
    public TablaCSV(Pane vPaneTabla) {
        this.vPaneTabla = vPaneTabla;
        inicializarTabla();
    }

    /**
     * Inicializa la tabla.
     */
    private void inicializarTabla() {
        tabla = new TableView<>();
        tabla.prefWidthProperty().bind(vPaneTabla.widthProperty());
        tabla.prefHeightProperty().bind(vPaneTabla.heightProperty());

        // Evita que JavaFX expanda columnas y respeta tu ancho real
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        vPaneTabla.getChildren().add(tabla);
    }

    /**
     * Carga los datos en la tabla.
     *
     * @param ventas Lista de ventas a mostrar
     */
    public void cargarDatos(List<Venta> ventas) {
        if (ventas == null || ventas.isEmpty()) {
            limpiar();
            return;
        }

        tabla.getColumns().clear();
        tabla.setItems(FXCollections.observableArrayList(ventas));

        // Crear columnas fijas según la clase Venta
        crearColumna("ID", "id");
        crearColumna("Nombre", "nombre");
        crearColumna("CIF", "cif");
        crearColumna("Email", "email");
        crearColumna("Producto", "producto");
        crearColumna("Precio", "precio");
        crearColumna("Fecha", "fecha");

        // Ajustar ancho de cada columna según su contenido
        tabla.getColumns().forEach(col -> autoFitColumn(col));
    }

    /**
     * Método auxiliar para crear columnas vinculadas a getters de Venta.
     *
     * @param titulo the titulo
     * @param propiedad the propiedad
     */
    private void crearColumna(String titulo, String propiedad) {
        TableColumn<Venta, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));

        // Ancho mínimo para que se vea bien
        col.setMinWidth(60);

        tabla.getColumns().add(col);
    }

    /**
     * Ajusta automáticamente el ancho de una columna al contenido.
     *
     * @param column the column
     */
    private void autoFitColumn(TableColumn<Venta, ?> column) {
        Text tempText = new Text(column.getText());
        double max = tempText.getLayoutBounds().getWidth();

        for (int i = 0; i < tabla.getItems().size(); i++) {
            Object cellData = column.getCellData(i);
            if (cellData != null) {
                tempText = new Text(cellData.toString());
                double width = tempText.getLayoutBounds().getWidth();
                if (width > max) {
                    max = width;
                }
            }
        }

        column.setPrefWidth(max + 20); // margen para padding interno
    }

    /**
     * Limpia la tabla.
     */
    public void limpiar() {
        if (tabla != null) {
            tabla.getItems().clear();
            tabla.getColumns().clear();
        }
    }

    /**
     * Obtiene la tabla.
     *
     * @return the tabla
     */
    public TableView<Venta> getTabla() {
        return tabla;
    }
}

