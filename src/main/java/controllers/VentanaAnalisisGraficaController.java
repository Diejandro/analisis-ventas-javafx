package controllers;

import models.RegistroCSV;
import models.Venta;
import services.AnalizadorVentas;
import services.DatosCSVService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.List;

import controllers.graficas.GraficoBarras;
import controllers.graficas.GraficoLinea;
import controllers.graficas.GraficoLinea.GrupoTiempo;
import controllers.graficas.GraficoPie;
import controllers.graficas.TablaCSV;

/**
 * Controlador para la vista de análisis gráfica
 * Maneja tabla, gráfico de barras, gráfico de línea y gráfico de pie.
 */
public class VentanaAnalisisGraficaController {

    /** The v pane tabla. */
    @FXML
    private Pane vPaneTabla;

    /** The bar chart. */
    @FXML
    private BarChart<String, Number> barChart;

    /** The line chart. */
    @FXML
    private LineChart<String, Number> lineChart;

    /** The pie chart. */
    @FXML
    private PieChart pieChart;

    /** The eje xfecha. */
    @FXML
    private CategoryAxis ejeXfecha;   // Para LineChart
    
    /** The eje yfacturacion. */
    @FXML
    private NumberAxis ejeYfacturacion;

    /** The eje X. */
    @FXML
    private CategoryAxis ejeX;         // Para BarChart
    
    /** The eje Y. */
    @FXML
    private NumberAxis ejeY;

    /** The rb day. */
    @FXML
    private RadioButton rbDay;
    
    /** The rb month. */
    @FXML
    private RadioButton rbMonth;
    
    /** The rb year. */
    @FXML
    private RadioButton rbYear;

    /** The tabla CSV. */
    private TablaCSV tablaCSV;
    
    /** The grafico barras. */
    private GraficoBarras graficoBarras;
    
    /** The grafico lineas. */
    private GraficoLinea graficoLineas;
    
    /** The grafico pie. */
    private GraficoPie graficoPie;
    
    /** The analizador. */
    private AnalizadorVentas analizador;
    
    /** The datos service. */
    private DatosCSVService datosService;

    /**
     * Inicializa el controlador que prepara el estado inicial de la vista.
     * <p>
     * Obtiene las vistas de la tabla y elementos gráficos, posteriormente, carga los datos
     * iniciales, los cuales mediante un listener se les da el contenido correspondiente y actualizar
     * los registros según se vayan cargando.
     */
    @FXML
    public void initialize() {
        datosService = DatosCSVService.getInstance();

        // Inicializar tabla y gráficos
        tablaCSV = new TablaCSV(vPaneTabla);
        graficoBarras = new GraficoBarras(barChart, ejeX, ejeY);
        graficoLineas = new GraficoLinea(lineChart, ejeXfecha, ejeYfacturacion);
        graficoPie = new GraficoPie(pieChart);

        // Cargar datos iniciales como Ventas
        List<Venta> ventasIniciales = datosService.obtenerVentas();
        if (ventasIniciales != null && !ventasIniciales.isEmpty()) {
            cargarDatos(ventasIniciales);
        }

        // Listener para actualizar automáticamente cuando cambien los registros CSV
        datosService.getDatos().addListener((ListChangeListener<RegistroCSV>) change -> {
            while (change.next()) {
                // Cada vez que hay cambios en los registros CSV, se refrescan los datos como Ventas
                List<Venta> ventasActualizadas = datosService.obtenerVentas();
                if (ventasActualizadas != null && !ventasActualizadas.isEmpty()) {
                    cargarDatos(ventasActualizadas);
                } else {
                    limpiarVista();
                }
            }
        });
    }

    /**
     * Carga los datos en la tabla y todos los gráficos.
     * <p>
     * Comprueba la existencia de datos cargados, carga los datos en los
     * elementos correspondientes mostrando en consola mensajes con los datos cargados y validados.
     *
     * @param ventas lista de datos de venta 
     */
    public void cargarDatos(List<Venta> ventas) {
        if (ventas == null || ventas.isEmpty()) {
            System.out.println("No hay datos para cargar");
            limpiarVista();
            return;
        }

        // Tabla
        tablaCSV.cargarDatos(ventas);

        // Analizador
        analizador = new AnalizadorVentas(ventas);

        // Gráficos
        graficoBarras.cargarDatos(analizador);
        graficoLineas.cargarDatos(analizador);
        graficoPie.cargarDatos(analizador);

        System.out.println("✓ Datos cargados: " + ventas.size() + " registros");
        System.out.println("✓ Total clientes únicos: " + analizador.obtenerTotalClientes());
        System.out.println("✓ Facturación total: €" + analizador.total("Precio"));
    }

    /**
     * Limpia tabla y gráficos.
     * <p>
     * Gestiona el contenido de la tabla y los gráficos para que no se solapen si se cargan otros archivos.
     */
    public void limpiarVista() {
        if (tablaCSV != null) tablaCSV.limpiar();
        if (graficoBarras != null) graficoBarras.limpiar();
        if (graficoLineas != null) graficoLineas.limpiar();
        if (graficoPie != null) graficoPie.limpiar();
        analizador = null;
        System.out.println("Vista limpiada");
    }

    /**
     * Maneja selección de agrupación temporal para el gráfico de líneas.
     * <p>
     * Gestiona el manejo de RadioButton para seleccionar el tipo de valores de tiempo
     * que se quiere mostrar en el gráfico.
     */
    @FXML
    void isSelected() {
        if (graficoLineas == null) return;

        if (rbDay.isSelected()) {
            graficoLineas.setAgrupacion(GrupoTiempo.DIA);
        } else if (rbMonth.isSelected()) {
            graficoLineas.setAgrupacion(GrupoTiempo.MES);
        } else if (rbYear.isSelected()) {
            graficoLineas.setAgrupacion(GrupoTiempo.ANIO);
        }
    }

    /**
     * Gets the tabla.
     *
     * @return the tabla
     */
    // Métodos auxiliares para acceder a la tabla y gráficos
    public TableView<Venta> getTabla() { return tablaCSV.getTabla(); }
    
    /**
     * Gets the bar chart.
     *
     * @return the bar chart
     */
    public BarChart<String, Number> getBarChart() { return graficoBarras.getBarChart(); }
    
    /**
     * Gets the line chart.
     *
     * @return the line chart
     */
    public LineChart<String, Number> getLineChart() { return graficoLineas.getLineChart(); }
    
    /**
     * Gets the pie chart.
     *
     * @return the pie chart
     */
    public PieChart getPieChart() { return graficoPie.getPieChart(); }
    
    /**
     * Gets the analizador.
     *
     * @return the analizador
     */
    public AnalizadorVentas getAnalizador() { return analizador; }
}
