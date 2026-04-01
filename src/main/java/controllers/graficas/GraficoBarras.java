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
 * Clase encargada de gestionar el gráfico de barras para visualizar 
 * el volumen de ventas por nivel de curso.
 */
public class GraficoBarras {

    private BarChart<String, Number> barChart;
    private NumberAxis ejeY;
    private AnalizadorVentas analizador;
    private DatosCSVService datosService;

    /**
     * Constructor del gestor de gráfico de barras.
     */
    public GraficoBarras(BarChart<String, Number> barChart, CategoryAxis ejeX, NumberAxis ejeY) {
        this.barChart = barChart;
        this.ejeY = ejeY;
        this.datosService = DatosCSVService.getInstance();
        configurarGraficoBarras();
    }

    /**
     * Configura las propiedades visuales y de escala del gráfico.
     */
    private void configurarGraficoBarras() {
        if (barChart == null) return;
        
        barChart.setLegendVisible(false); 
        barChart.setAnimated(true);

        ejeY.setLabel("Cantidad de Ventas");
        ejeY.setAutoRanging(true);
        ejeY.setMinorTickVisible(false);
        
        // Formateador para asegurar que el eje Y solo muestre números enteros (ventas)
        ejeY.setTickLabelFormatter(new NumberAxis.DefaultFormatter(ejeY) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });
    }

    public void cargarDatos(AnalizadorVentas analizador) {
        this.analizador = analizador;
        actualizarGraficoBarras();
    }

    /**
     * Procesa los datos del analizador y los vuelca en el gráfico.
     * Mantiene un orden fijo: Básico -> Intermedio -> Avanzado.
     */
    private void actualizarGraficoBarras() {
        if (analizador == null || barChart == null) return;

        barChart.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        
        // CORRECCIÓN: Nombres sincronizados con el CSV (Mayúsculas)
        String[] niveles = {"Curso Básico", "Curso Intermedio", "Curso Avanzado"};

        for (String curso : niveles) {
            int cantidad = analizador.flujoVentasPorProducto(curso);
            serie.getData().add(new XYChart.Data<>(curso, cantidad));
        }

        barChart.getData().add(serie);

        // Aplicamos los colores corporativos a las barras
        aplicarColoresBarras(serie);
    }

    /**
     * Restaura el gráfico a su estado de visualización de ventas.
     */
    public void restaurarGraficoVentas() {
        if (barChart != null) {
            barChart.setTitle("Volumen de Ventas por Nivel");
            actualizarGraficoBarras();
        }
    }

    public void limpiar() {
        if (barChart != null) barChart.getData().clear();
        this.analizador = null;
    }

    /**
     * Sincroniza el gráfico con el estado actual del servicio de datos.
     */
    public void actualizar() {
        if (analizador != null) {
            actualizarGraficoBarras();
        } else if (!datosService.obtenerVentas().isEmpty()) {
            this.analizador = new AnalizadorVentas(datosService.obtenerVentas());
            actualizarGraficoBarras();
        }
    }

    /**
     * Asigna colores específicos a cada barra para facilitar la lectura visual.
     */
    private void aplicarColoresBarras(XYChart.Series<String, Number> serie) {
        // Necesario usar Platform.runLater porque los nodos de las barras 
        // se crean justo después de añadir la serie al gráfico.
        Platform.runLater(() -> {
            for (int i = 0; i < serie.getData().size(); i++) {
                XYChart.Data<String, Number> dato = serie.getData().get(i);
                Node barra = dato.getNode();
                
                if (barra != null) {
                    String color = switch (i) {
                        case 0 -> "#4da8da"; // Azul claro (Básico)
                        case 1 -> "#0e1a2a"; // Azul oscuro (Intermedio)
                        case 2 -> "#2c3e50"; // Gris azulado (Avanzado)
                        default -> "#bfc9d6";
                    };
                    barra.setStyle("-fx-bar-fill: " + color + ";");
                }
            }
        });
    }

    public BarChart<String, Number> getBarChart() {
        return barChart;
    }
}