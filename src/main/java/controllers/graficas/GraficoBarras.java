package controllers.graficas;

import services.AnalizadorVentas;
import services.DatosCSVService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Clase encargada de gestionar el gráfico de barras.
 */
public class GraficoBarras {

    /** The bar chart. */
    private BarChart<String, Number> barChart;
    
    /** The eje Y. */
    private NumberAxis ejeY;
    
    /** The analizador. */
    private AnalizadorVentas analizador;
    
    /** The datos service. */
    private DatosCSVService datosService;

    /**
     * Constructor.
     *
     * @param barChart the bar chart
     * @param ejeX the eje X
     * @param ejeY the eje Y
     */
    public GraficoBarras(BarChart<String, Number> barChart, CategoryAxis ejeX, NumberAxis ejeY) {
        this.barChart = barChart;
        this.ejeY = ejeY;
        this.datosService = DatosCSVService.getInstance();
        configurarGraficoBarras();
    }

    /**
     * Configura las propiedades iniciales del gráfico de barras.
     */
    private void configurarGraficoBarras() {
        barChart.setLegendVisible(false); 
        barChart.setAnimated(true);

        ejeY.setAutoRanging(true);
        ejeY.setMinorTickVisible(false);
        ejeY.setTickUnit(1); 
    }

    /**
     * Carga los datos y actualiza el gráfico.
     *
     * @param analizador the analizador
     */
    public void cargarDatos(AnalizadorVentas analizador) {
        this.analizador = analizador;
        actualizarGraficoBarras();
    }

    /**
     * Actualiza el gráfico de barras con las ventas por producto.
     * <p>
     * Limpia los elementos dentro de la barra, y cuando se encuentren los datos presentes,
     * completa las barras dandoles contenido y forma.
     */
    private void actualizarGraficoBarras() {
        if (analizador == null) return;

        barChart.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        String[] productosOrdenados = {"Curso básico", "Curso intermedio", "Curso avanzado"};

        int maxCantidad = 0;
        for (String producto : productosOrdenados) {
            int cantidad = analizador.flujoVentasPorProducto(producto);
            if (cantidad > maxCantidad) maxCantidad = cantidad;
            serie.getData().add(new XYChart.Data<>(producto, cantidad));
        }

        barChart.getData().add(serie);

        ejeY.setTickLabelFormatter(new NumberAxis.DefaultFormatter(ejeY) {
            @Override
            public String toString(Number object) {
                return String.valueOf(object.intValue());
            }
        });

        aplicarColoresBarras(serie);
    }

    /**
     * Establece al gráfico un orden específico.
     *
     */
    public void actualizarGraficoOrdenado() {
        if (analizador == null) return;

        barChart.getData().clear();
        String[] productosOrdenados = {"Curso básico", "Curso intermedio", "Curso avanzado"};
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        for (String producto : productosOrdenados) {
            int cantidad = analizador.flujoVentasPorProducto(producto);
            serie.getData().add(new XYChart.Data<>(producto, cantidad));
        }

        barChart.getData().add(serie);
    }

    /**
     * Restaura gráfico al modo ventas.
     */
    public void restaurarGraficoVentas() {
        barChart.setTitle("Ventas por Producto");
        ejeY.setLabel("Cantidad de Ventas");
        actualizarGraficoBarras();
    }

    /**
     * Limpia el gráfico para que no se solapen.
     */
    public void limpiar() {
        if (barChart != null) barChart.getData().clear();
        analizador = null;
    }

    /**
     * Actualiza solo el gráfico cada vez que se modifiquen los datos.
     */
    public void actualizar() {
        if (analizador != null) {
            actualizarGraficoBarras();
        } else if (!datosService.obtenerVentas().isEmpty()) {
            analizador = new AnalizadorVentas(datosService.obtenerVentas());
            actualizarGraficoBarras();
        }
    }

    /**
     * Aplicar colores barras.
     *
     * @param serie the serie
     */
    private void aplicarColoresBarras(XYChart.Series<String, Number> serie) {
        Platform.runLater(() -> {
            for (int i = 0; i < serie.getData().size(); i++) {
                XYChart.Data<String, Number> dato = serie.getData().get(i);
                Node barra = dato.getNode();
                if (barra != null) {
                    String color;
                    switch (i) {
                        case 0: color = "#ff4d4d"; break; 
                        case 1: color = "#4da6ff"; break; 
                        case 2: color = "#6dd56d"; break; 
                        default: color = "#ffaa00"; break;
                    }
                    barra.setStyle("-fx-bar-fill: " + color + ";");
                }
            }
        });
    }

    /**
     * Gets the bar chart.
     *
     * @return the bar chart
     */
    public BarChart<String, Number> getBarChart() {
        return barChart;
    }
}
