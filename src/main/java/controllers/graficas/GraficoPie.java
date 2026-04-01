package controllers.graficas;

import models.Venta;
import models.Producto; // Import necesario
import services.AnalizadorVentas;
import services.DatosCSVService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que gestiona la visualización y actualización del gráfico de sectores (PieChart).
 * Representa la distribución de la facturación por cada producto.
 */
public class GraficoPie {

    private PieChart pieChart;
    private AnalizadorVentas analizador;
    private DatosCSVService datosService;

    /**
     * Constructor que vincula el componente de la vista y prepara el servicio de datos.
     * * @param pieChart El componente PieChart definido en el FXML.
     */
    public GraficoPie(PieChart pieChart) {
        this.pieChart = pieChart;
        this.datosService = DatosCSVService.getInstance();
        configurarGraficoPie();
    }

    /**
     * Establece la configuración visual inicial del gráfico.
     */
    private void configurarGraficoPie() {
        if (pieChart != null) {
            pieChart.setLegendVisible(true);
            pieChart.setClockwise(true);
            pieChart.setLabelsVisible(true);
            pieChart.setStartAngle(90);
            pieChart.setTitle("Distribución de Facturación");
        }
    }

    /**
     * Carga un nuevo analizador y refresca los datos visuales.
     * * @param analizador El analizador con la lista de ventas actualizada.
     */
    public void cargarDatos(AnalizadorVentas analizador) {
        this.analizador = analizador;
        actualizarGraficoPie();
    }

    /**
     * Procesa las ventas para agrupar la facturación por nombre de producto
     * y genera las porciones (slices) del gráfico.
     */
    private void actualizarGraficoPie() {
        if (analizador == null || pieChart == null) {
            return;
        }

        pieChart.getData().clear();

        // Obtener ventas actuales desde el servicio
        List<Venta> ventas = datosService.obtenerVentas();

        if (ventas.isEmpty()) {
            return;
        }

        // Mapa para acumular la facturación por NOMBRE de producto
        Map<String, Double> facturacionPorProducto = new HashMap<>();

        // CORRECCIÓN: Extraemos el nombre del Record Producto para que Analizador lo entienda
        ventas.stream()
                .map(Venta::getProducto) // Obtenemos el objeto Producto
                .filter(p -> p != null && !p.nombre().isBlank()) // Validamos el Record
                .map(Producto::nombre) // Nos quedamos solo con el String del nombre
                .distinct()
                .forEach(nombre -> {
                    double totalProducto = analizador.flujoFacturacionProducto(nombre);
                    if (totalProducto > 0) {
                        facturacionPorProducto.put(nombre, totalProducto);
                    }
                });

        if (facturacionPorProducto.isEmpty()) {
            return;
        }

        // Calcular el total para obtener porcentajes
        double sumaTotal = facturacionPorProducto.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        ObservableList<PieChart.Data> listaDatos = FXCollections.observableArrayList();

        // Crear las porciones del gráfico
        facturacionPorProducto.forEach((nombre, valor) -> {
            double porcentaje = (valor / sumaTotal) * 100;
            // Etiqueta con nombre y porcentaje formateado
            String etiqueta = String.format("%s (%.1f%%)", nombre, porcentaje);
            listaDatos.add(new PieChart.Data(etiqueta, valor));
        });

        pieChart.setData(listaDatos);
    }

    /**
     * Limpia los datos del gráfico y el analizador.
     */
    public void limpiar() {
        if (pieChart != null) {
            pieChart.getData().clear();
        }
        this.analizador = null;
    }

    /**
     * Sincroniza el gráfico con el estado actual del servicio de datos.
     */
    public void actualizar() {
        List<Venta> ventas = datosService.obtenerVentas();
        if (!ventas.isEmpty()) {
            this.analizador = new AnalizadorVentas(ventas);
            actualizarGraficoPie();
        } else {
            limpiar();
        }
    }

    public PieChart getPieChart() {
        return pieChart;
    }
}