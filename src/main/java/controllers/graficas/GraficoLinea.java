package controllers.graficas;

import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import models.Venta;
import services.DatosCSVService;
import services.AnalizadorVentas;

/**
 * Gestiona el gráfico de líneas para mostrar la evolución de la facturación.
 * Incluye correcciones de renderizado para evitar el apiñamiento de etiquetas en el eje X.
 */
public class GraficoLinea {

    public enum GrupoTiempo {
        DIA, MES, ANIO
    }

    private LineChart<String, Number> lineChart;
    private CategoryAxis ejeX;
    private NumberAxis ejeY;
    private AnalizadorVentas analizador;
    private DatosCSVService datosService;
    private GrupoTiempo modoAgrupacion = GrupoTiempo.MES;

    /**
     * Constructor del gestor de gráfico de líneas.
     */
    public GraficoLinea(LineChart<String, Number> lineChart, CategoryAxis ejeX, NumberAxis ejeY) {
        this.lineChart = lineChart;
        this.ejeX = ejeX;
        this.ejeY = ejeY;
        this.datosService = DatosCSVService.getInstance();
        configurarGraficoLineas();
    }

    /**
     * Configuración técnica para evitar errores visuales de JavaFX.
     */
    private void configurarGraficoLineas() {
        if (lineChart == null) return;
        
        lineChart.setLegendVisible(false);
        
        // Desactivar animaciones en el gráfico y en el eje X evita que las etiquetas 
        // se amontonen en el origen (0,0) al cargar los datos.
        lineChart.setAnimated(false); 
        ejeX.setAnimated(false);
        
        lineChart.setCreateSymbols(true); 

        ejeX.setLabel("Periodo Temporal");
        ejeX.setTickLabelGap(10); 

        ejeY.setLabel("Facturación Acumulada (€)");
        ejeY.setAutoRanging(true);
        ejeY.setForceZeroInRange(true); 
    }

    public void setAgrupacion(GrupoTiempo modo) {
        this.modoAgrupacion = modo;
        actualizar();
    }

    public void cargarDatos(AnalizadorVentas analizador) {
        this.analizador = analizador;
        actualizarGraficoLineas();
    }

    /**
     * Refresca el gráfico limpiando categorías y datos para asegurar una distribución uniforme.
     */
    private void actualizarGraficoLineas() {
        if (analizador == null || lineChart == null) return;

        // 1. Limpieza absoluta de series y categorías del eje
        lineChart.getData().clear();
        ejeX.getCategories().clear();

        Map<String, Double> datosAgrupados = agruparFacturacion(modoAgrupacion);
        if (datosAgrupados.isEmpty()) return;

        // 2. Definir las categorías explícitamente antes de añadir los datos
        // Esto obliga al eje X a calcular el espacio ANTES de dibujar la línea
        ejeX.setCategories(FXCollections.observableArrayList(datosAgrupados.keySet()));

        // Rotación dinámica para evitar solapamiento horizontal
        if (datosAgrupados.size() > 5) {
            ejeX.setTickLabelRotation(45);
        } else {
            ejeX.setTickLabelRotation(0);
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Facturación");

        // 3. Poblar la serie
        datosAgrupados.forEach((periodo, valor) -> {
            serie.getData().add(new XYChart.Data<>(periodo, valor));
        });

        lineChart.getData().add(serie);

        // 4. Instalación de Tooltips
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> d : serie.getData()) {
                if (d.getNode() != null) {
                    String mensaje = String.format("%s: %.2f €", d.getXValue(), d.getYValue().doubleValue());
                    Tooltip.install(d.getNode(), new Tooltip(mensaje));
                }
            }
        });
    }

    /**
     * Lógica de agrupación de ventas.
     */
    private Map<String, Double> agruparFacturacion(GrupoTiempo modo) {
        List<Venta> ventas = datosService.obtenerVentas();

        return switch (modo) {
            case DIA -> ventas.stream()
                    .filter(v -> v.getFecha() != null)
                    .collect(Collectors.groupingBy(
                            v -> v.getFecha().toString(),
                            TreeMap::new,
                            Collectors.summingDouble(Venta::getPrecio)
                    ));

            case MES -> ventas.stream()
                    .filter(v -> v.getFecha() != null)
                    .collect(Collectors.groupingBy(
                            v -> YearMonth.from(v.getFecha()).toString(),
                            TreeMap::new,
                            Collectors.summingDouble(Venta::getPrecio)
                    ));

            case ANIO -> ventas.stream()
                    .filter(v -> v.getFecha() != null)
                    .collect(Collectors.groupingBy(
                            v -> String.valueOf(v.getFecha().getYear()),
                            TreeMap::new,
                            Collectors.summingDouble(Venta::getPrecio)
                    ));
        };
    }

    public void limpiar() {
        if (lineChart != null) lineChart.getData().clear();
        this.analizador = null;
    }

    public void actualizar() {
        List<Venta> ventas = datosService.obtenerVentas();
        if (!ventas.isEmpty()) {
            this.analizador = new AnalizadorVentas(ventas);
            actualizarGraficoLineas();
        } else {
            limpiar();
        }
    }

    public LineChart<String, Number> getLineChart() {
        return lineChart;
    }
}