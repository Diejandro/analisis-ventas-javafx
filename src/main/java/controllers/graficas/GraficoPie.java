package controllers.graficas;

import models.Venta;
import services.AnalizadorVentas;
import services.DatosCSVService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Clase que gestiona un PieChart de facturación por producto.
 */
public class GraficoPie {

    /** The pie chart. */
    private PieChart pieChart;
    
    /** The analizador. */
    private AnalizadorVentas analizador;
    
    /** The datos service. */
    private DatosCSVService datosService;

    /**
     * Constructor.
     *
     * @param pieChart the pie chart
     */
    public GraficoPie(PieChart pieChart) {
        this.pieChart = pieChart;
        this.datosService = DatosCSVService.getInstance();
        configurarGraficoPie();
    }

    /**
     * Configura las propiedades iniciales del gráfico.
     */
    private void configurarGraficoPie() {
        pieChart.setLegendVisible(true);
        pieChart.setClockwise(true);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(90);
    }

    /**
     * Carga los datos en el gráfico.
     *
     * @param analizador the analizador
     */
    public void cargarDatos(AnalizadorVentas analizador) {
        this.analizador = analizador;
        actualizarGraficoPie();
    }

    /**
     * Actualiza el gráfico de pie sumando facturación por producto.
     */
    private void actualizarGraficoPie() {
        if (analizador == null) {
            System.out.println("No hay analizador configurado");
            return;
        }

        pieChart.getData().clear();

        // Obtener ventas
        List<Venta> ventas = datosService.obtenerVentas();

        // Productos únicos y su facturación
        Map<String, Double> facturacionPorProducto = new HashMap<>();
        ventas.stream()
                .map(Venta::getProducto)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .forEach(producto -> facturacionPorProducto.put(producto, analizador.flujoFacturacionProducto(producto)));

        if (facturacionPorProducto.isEmpty()) {
            System.out.println("No hay datos para graficar");
            return;
        }

        // Crear lista de datos para PieChart
        ObservableList<PieChart.Data> listaDatos = FXCollections.observableArrayList();

        double totalFacturacion = facturacionPorProducto.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        facturacionPorProducto.forEach((producto, valor) -> {
            double porcentaje = (valor / totalFacturacion) * 100;
            PieChart.Data slice = new PieChart.Data(producto + " (" + String.format("%.1f%%", porcentaje) + ")", valor);
            listaDatos.add(slice);
        });

        pieChart.setData(listaDatos);
        //System.out.println("✓ Gráfico de pie actualizado con " + facturacionPorProducto.size() + " productos");
    }

    /**
     * Limpia el gráfico.
     */
    public void limpiar() {
        if (pieChart != null) pieChart.getData().clear();
        analizador = null;
    }

    /**
     * Refresca el gráfico.
     */
    public void actualizar() {
        if (analizador != null) {
            actualizarGraficoPie();
        } else {
            List<Venta> ventas = datosService.obtenerVentas();
            if (!ventas.isEmpty()) {
                analizador = new AnalizadorVentas(ventas);
                actualizarGraficoPie();
            }
        }
    }

    /**
     * Devuelve el PieChart.
     *
     * @return the pie chart
     */
    public PieChart getPieChart() {
        return pieChart;
    }
}


