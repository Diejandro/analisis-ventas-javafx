package controllers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.ClienteEstadistica;
import models.Venta;
import models.RegistroCSV;
import services.AnalizadorVentas;
import services.DatosCSVService;

/**
 * The Class VentanaAnalisisGeneralController.
 */
public class VentanaAnalisisGeneralController {

    /** The analizador ventas. */
    private AnalizadorVentas analizadorVentas;

    /** The l facturacion avanzado. */
    @FXML
    private Label lFacturacionAvanzado;
    
    /** The l facturacion basico. */
    @FXML
    private Label lFacturacionBasico;
    
    /** The l facturacion intermedio. */
    @FXML
    private Label lFacturacionIntermedio;
    
    /** The l general clientes total. */
    @FXML
    private Label lGeneralClientesTotal;
    
    /** The l general facturacion total. */
    @FXML
    private Label lGeneralFacturacionTotal;
    
    /** The l general productos total. */
    @FXML
    private Label lGeneralProductosTotal;
    
    /** The l general promedio total. */
    @FXML
    private Label lGeneral_PromedioTotal;
    
    /** The l ventas avanzado. */
    @FXML
    private Label lVentasAvanzado;
    
    /** The l ventas basico. */
    @FXML
    private Label lVentasBasico;
    
    /** The l ventas intermedio. */
    @FXML
    private Label lVentasIntermedio;
    
    /** The mail destacado 1. */
    @FXML
    private Label mailDestacado1;
    
    /** The mail destacado 2. */
    @FXML
    private Label mailDestacado2;
    
    /** The mail destacado 3. */
    @FXML
    private Label mailDestacado3;
    
    /** The mail destacado 4. */
    @FXML
    private Label mailDestacado4;
    
    /** The mail destacado 5. */
    @FXML
    private Label mailDestacado5;
    
    /** The nombre destacado 1. */
    @FXML
    private Label nombreDestacado1;
    
    /** The nombre destacado 2. */
    @FXML
    private Label nombreDestacado2;
    
    /** The nombre destacado 3. */
    @FXML
    private Label nombreDestacado3;
    
    /** The nombre destacado 4. */
    @FXML
    private Label nombreDestacado4;
    
    /** The nombre destacado 5. */
    @FXML
    private Label nombreDestacado5;
    
    /** The v box general. */
    @FXML
    private VBox vBox_general;

    /**
     * Inicializa el controlador y prepara el estado inicial de la vista.
     * <p>
     * Obtiene las ventas actuales desde el servicio de datos, inicializa
     * el analizador de ventas y registra un listener para reaccionar a
     * cambios en los datos. Ante cualquier modificación, se recalculan
     * los valores y se actualizan los distintos componentes de la vista.
     */
    @FXML
    public void initialize() {
        // Inicializar analizador con las ventas actuales
        List<Venta> ventasIniciales = DatosCSVService.getInstance().obtenerVentas();
        analizadorVentas = new AnalizadorVentas(ventasIniciales);

        // Listener para actualizar cuando se agreguen o modifiquen registros
        DatosCSVService.getInstance()
                .getDatos()
                .addListener((ListChangeListener<RegistroCSV>) c -> {
                    List<Venta> ventasActualizadas = DatosCSVService.getInstance().obtenerVentas();
                    analizadorVentas = new AnalizadorVentas(ventasActualizadas);

                    actualizarTotales();
                    actualizarFlujoVentas();
                    actualizarFacturacionVentas();
                    actualizarTopClientes();
                });

        // Primer renderizado
        actualizarTotales();
        actualizarFlujoVentas();
        actualizarFacturacionVentas();
        actualizarTopClientes();
    }

    /**
     * Actualiza totales establecinedo valores a las etiquetas correspondientes.
     */
    public void actualizarTotales() {
        lGeneralClientesTotal.setText(String.valueOf(analizadorVentas.obtenerTotalClientes()));
        lGeneralProductosTotal.setText(String.valueOf(analizadorVentas.obtenerTotalVentas()));
        lGeneral_PromedioTotal.setText(formatearEuro(analizadorVentas.promedio("Precio")));
        lGeneralFacturacionTotal.setText(formatearEuro(analizadorVentas.total("Precio")));
    }

    /**
     * Actualiza flujo ventas estableciendo valores a etiquetas correspondientes.
     */
    public void actualizarFlujoVentas() {
        lVentasBasico.setText(String.valueOf(analizadorVentas.flujoVentasPorProducto("Curso básico")));
        lVentasIntermedio.setText(String.valueOf(analizadorVentas.flujoVentasPorProducto("Curso intermedio")));
        lVentasAvanzado.setText(String.valueOf(analizadorVentas.flujoVentasPorProducto("Curso avanzado")));
    }

    /**
     * Actualiza facturacion ventas estableciendo valores a etiquetas correspondientes.
     */
    public void actualizarFacturacionVentas() {
        lFacturacionBasico.setText(formatearEuro(analizadorVentas.flujoFacturacionProducto("Curso básico")));
        lFacturacionIntermedio.setText(formatearEuro(analizadorVentas.flujoFacturacionProducto("Curso intermedio")));
        lFacturacionAvanzado.setText(formatearEuro(analizadorVentas.flujoFacturacionProducto("Curso avanzado")));
    }

    /**
     * Actualiza un ranking de 5 clientes con más ventas.
     * <p>
     * En una lista se cargan los clientes y se asignan detalles como nombre y email de cada uno de ellos
     * en la vista general, ordenandolos además de mayor a menos número de adquisiciones.
     */
    public void actualizarTopClientes() {
        List<ClienteEstadistica> top5 = analizadorVentas.topClientes(5);

        Label[] nombres = {nombreDestacado1, nombreDestacado2, nombreDestacado3, nombreDestacado4, nombreDestacado5};
        Label[] emails = {mailDestacado1, mailDestacado2, mailDestacado3, mailDestacado4, mailDestacado5};

        for (int i = 0; i < 5; i++) {
            if (i < top5.size()) {
                ClienteEstadistica c = top5.get(i);
                nombres[i].setText(c.getNombre());
                emails[i].setText(c.getEmail());
            } else {
                nombres[i].setText("-");
                emails[i].setText("-");
            }
        }
    }

    /**
     * Dar formato a los valores
     * <p>
     * Convierte un valor numérico en una representación textual con
     * separador de miles, dos decimales y símbolo de euro, utilizando
     * el formato habitual en España.
     *
     * @param valor el valo numérico que será formateado
     * @return el valor en formateado en forma de cadena de texto
     */
    public static String formatearEuro(double valor) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.00", symbols);
        return df.format(valor) + " €";
    }
}
