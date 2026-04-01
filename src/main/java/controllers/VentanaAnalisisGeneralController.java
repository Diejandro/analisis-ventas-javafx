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
 * Controlador para la vista de análisis general.
 * Se encarga de mostrar KPIs, flujos de productos y el ranking de clientes.
 */
public class VentanaAnalisisGeneralController {

    private AnalizadorVentas analizadorVentas;

    @FXML private Label lFacturacionAvanzado;
    @FXML private Label lFacturacionBasico;
    @FXML private Label lFacturacionIntermedio;
    @FXML private Label lGeneralClientesTotal;
    @FXML private Label lGeneralFacturacionTotal;
    @FXML private Label lGeneralProductosTotal;
    @FXML private Label lGeneral_PromedioTotal;
    @FXML private Label lVentasAvanzado;
    @FXML private Label lVentasBasico;
    @FXML private Label lVentasIntermedio;
    
    @FXML private Label mailDestacado1, mailDestacado2, mailDestacado3, mailDestacado4, mailDestacado5;
    @FXML private Label nombreDestacado1, nombreDestacado2, nombreDestacado3, nombreDestacado4, nombreDestacado5;
    
    @FXML private VBox vBox_general;

    /**
     * Inicializa el controlador y configura el listener para cambios en tiempo real.
     */
    @FXML
    public void initialize() {
        configurarAnalizador();

        // Listener para reaccionar a cambios en los datos del CSV (añadir/borrar filas)
        DatosCSVService.getInstance().getDatos().addListener((ListChangeListener<RegistroCSV>) c -> {
            configurarAnalizador();
            refrescarVista();
        });

        // Renderizado inicial
        refrescarVista();
    }

    /**
     * Extrae las ventas del servicio y actualiza la instancia del analizador.
     */
    private void configurarAnalizador() {
        List<Venta> ventasActuales = DatosCSVService.getInstance().obtenerVentas();
        this.analizadorVentas = new AnalizadorVentas(ventasActuales);
    }

    /**
     * Agrupa todas las llamadas de actualización de la UI.
     */
    private void refrescarVista() {
        actualizarTotales();
        actualizarFlujoVentas();
        actualizarFacturacionVentas();
        actualizarTopClientes();
    }

    public void actualizarTotales() {
        lGeneralClientesTotal.setText(String.valueOf(analizadorVentas.obtenerTotalClientes()));
        lGeneralProductosTotal.setText(String.valueOf(analizadorVentas.obtenerTotalVentas()));
        lGeneral_PromedioTotal.setText(formatearEuro(analizadorVentas.promedio("Precio")));
        lGeneralFacturacionTotal.setText(formatearEuro(analizadorVentas.total("Precio")));
    }

    /**
     * Nota: Asegúrate de que los nombres de los productos en tu CSV 
     * coincidan exactamente con estos Strings.
     */
    public void actualizarFlujoVentas() {
        lVentasBasico.setText(String.valueOf(analizadorVentas.flujoVentasPorProducto("Curso básico")));
        lVentasIntermedio.setText(String.valueOf(analizadorVentas.flujoVentasPorProducto("Curso intermedio")));
        lVentasAvanzado.setText(String.valueOf(analizadorVentas.flujoVentasPorProducto("Curso avanzado")));
    }

    public void actualizarFacturacionVentas() {
        lFacturacionBasico.setText(formatearEuro(analizadorVentas.flujoFacturacionProducto("Curso básico")));
        lFacturacionIntermedio.setText(formatearEuro(analizadorVentas.flujoFacturacionProducto("Curso intermedio")));
        lFacturacionAvanzado.setText(formatearEuro(analizadorVentas.flujoFacturacionProducto("Curso avanzado")));
    }

    /**
     * Actualiza el ranking visual de los 5 mejores alumnos.
     */
    public void actualizarTopClientes() {
        List<ClienteEstadistica> top5 = analizadorVentas.topClientes(5);

        Label[] nombres = {nombreDestacado1, nombreDestacado2, nombreDestacado3, nombreDestacado4, nombreDestacado5};
        Label[] emails = {mailDestacado1, mailDestacado2, mailDestacado3, mailDestacado4, mailDestacado5};

        for (int i = 0; i < 5; i++) {
            if (i < top5.size()) {
                ClienteEstadistica c = top5.get(i);
                // CAMBIO CLAVE: Acceso a métodos de Record (sin 'get')
                nombres[i].setText(c.nombre());
                emails[i].setText(c.email());
            } else {
                nombres[i].setText("-");
                emails[i].setText("-");
            }
        }
    }

    /**
     * Utilidad para formatear moneda.
     */
    public static String formatearEuro(double valor) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.00", symbols);
        return df.format(valor) + " €";
    }
}