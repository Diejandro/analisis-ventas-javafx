package controllers.graficas;

import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import models.Venta;
import services.DatosCSVService;
import services.AnalizadorVentas;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class GraficoLinea.
 */
public class GraficoLinea {

    /**
     * The Enum GrupoTiempo.
     */
    public enum GrupoTiempo {
        
        /** The dia. */
        DIA,
        
        /** The mes. */
        MES,
        
        /** The anio. */
        ANIO
    }

    /** The line chart. */
    private LineChart<String, Number> lineChart;
    
    /** The eje X. */
    private CategoryAxis ejeX;
    
    /** The eje Y. */
    private NumberAxis ejeY;

    /** The analizador. */
    private AnalizadorVentas analizador;
    
    /** The datos service. */
    private DatosCSVService datosService;

    /** The modo agrupacion. */
    private GrupoTiempo modoAgrupacion = GrupoTiempo.MES; // Por defecto

    /**
     * Instancia un gráfico de linea.
     *
     * @param lineChart the line chart
     * @param ejeX the eje X
     * @param ejeY the eje Y
     */
    public GraficoLinea(LineChart<String, Number> lineChart, CategoryAxis ejeX, NumberAxis ejeY) {
        this.lineChart = lineChart;
        this.ejeX = ejeX;
        this.ejeY = ejeY;
        this.datosService = DatosCSVService.getInstance();

        configurarGraficoLineas();
    }

    /**
     * Configurar grafico lineas dando algunos detalles sencillos.
     */
    private void configurarGraficoLineas() {
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(true);

        ejeX.setLabel("Fecha");
        ejeY.setLabel("Facturación (€)");
        ejeY.setAutoRanging(true);
    }

    /**
     * Establece la agrupación
     *
     * @param modo the new agrupacion
     */
    public void setAgrupacion(GrupoTiempo modo) {
        this.modoAgrupacion = modo;
        actualizar();
    }

    /**
     * Cargar datos y actualiza el gráfico.
     *
     * @param analizador the analizador
     */
    public void cargarDatos(AnalizadorVentas analizador) {
        this.analizador = analizador;
        actualizarGraficoLineas();
    }

    /**
     * Actualiza el gráfico de lienas.
     */
    private void actualizarGraficoLineas() {
        if (analizador == null) {
            System.out.println("No hay analizador configurado");
            return;
        }

        lineChart.getData().clear();

        Map<String, Double> datosAgrupados = agruparFacturacion(modoAgrupacion);

        if (datosAgrupados.isEmpty()) {
            System.out.println("No hay datos válidos para graficar");
            return;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Facturación");

        datosAgrupados.forEach((periodo, valor) -> {
            XYChart.Data<String, Number> punto = new XYChart.Data<>(periodo, valor);

            Platform.runLater(() -> {
                if (punto.getNode() != null) {
                    javafx.scene.control.Tooltip.install(
                            punto.getNode(),
                            new javafx.scene.control.Tooltip(periodo + ": €" + valor)
                    );
                }
            });

            serie.getData().add(punto);
        });

        lineChart.getData().add(serie);
        //System.out.println("✓ Gráfico actualizado (" + modoAgrupacion + ")");
    }

    /**
     * Agrupación por día, mes o año.
     *
     * @param modo the modo
     * @return the map
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
                            v -> {
                                YearMonth ym = YearMonth.from(v.getFecha());
                                return ym.toString(); // Ej: 2025-03
                            },
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

    /**
     * Limpiar.
     */
    public void limpiar() {
        if (lineChart != null)
            lineChart.getData().clear();
        analizador = null;
    }

    /**
     * Actualizar.
     */
    public void actualizar() {
        if (analizador != null) {
            actualizarGraficoLineas();
        } else {
            List<Venta> ventas = datosService.obtenerVentas();
            if (!ventas.isEmpty()) {
                analizador = new AnalizadorVentas(ventas);
                actualizarGraficoLineas();
            }
        }
    }

    /**
     * Gets the line chart.
     *
     * @return the line chart
     */
    public LineChart<String, Number> getLineChart() {
        return lineChart;
    }
}

