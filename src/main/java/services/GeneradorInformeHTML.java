package services;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import models.ClienteEstadistica;
import models.Venta;

// TODO: Auto-generated Javadoc
/**
 * The Class GeneradorInformeHTML.
 */
public class GeneradorInformeHTML {

    /** The av. */
    private AnalizadorVentas av;
    
    /** The datos. */
    private List<Venta> datos;

    /** The Constant symbols. */
    // Formato europeo
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
    
    /** The Constant dfNumber. */
    private static final DecimalFormat dfNumber = new DecimalFormat("#,##0.00", symbols);

    /**
     * Instantiates a new generador informe HTML.
     *
     * @param datos the datos
     */
    public GeneradorInformeHTML(List<Venta> datos) {
        this.datos = datos;
        this.av = new AnalizadorVentas(datos);
    }

    /**
     * Genera el HTML como String, usando AnalizadorVentas.
     *
     * @return the string
     */
    public String generarInformeComoString() {

        // -----------------------------
        // Datos generales
        // -----------------------------
        int totalVentas = av.obtenerTotalVentas();
        double ingresosTotales = av.total("Precio");
        double precioMedio = totalVentas > 0 ? ingresosTotales / totalVentas : 0;

        List<ClienteEstadistica> topClientes = av.topClientes(10);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // -----------------------------
        // Construcción del HTML
        // -----------------------------
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang='es'>")
            .append("<head>")
            .append("<meta charset='UTF-8'>")
            .append("<title>Informe de Ventas</title>")
            .append("<style>")
            .append("body { font-family: Arial; margin: 24px; background:#f7f8fa; color:#111; }")
            .append(".card { background:white; border-radius:12px; padding:18px; margin-bottom:20px; box-shadow:0 6px 18px rgba(0,0,0,0.06); }")
            .append(".kpi-box { display:flex; gap:30px; margin-top:10px; }")
            .append(".kpi { font-size:18px; font-weight:bold; }")
            .append("table { width:100%; border-collapse:collapse; margin-top:10px; }")
            .append("th, td { border-bottom:1px solid #ddd; padding:8px 10px; text-align:left; }")
            .append("th { background:#eceff5; }")
            .append("</style>")
            .append("</head>")
            .append("<body>");

        // -----------------------------
        // Resumen general
        // -----------------------------
        html.append("<div class='card'>")
            .append("<h1>Informe de ventas — Cursos de Inglés</h1>")
            .append("<div class='kpi-box'>")
            .append("<div class='kpi'>Total ventas: ").append(totalVentas).append("</div>")
            .append("<div class='kpi'>Ingresos: ").append(dfNumber.format(ingresosTotales)).append(" €</div>")
            .append("<div class='kpi'>Precio medio: ").append(dfNumber.format(precioMedio)).append(" €</div>")
            .append("</div></div>");

        // -----------------------------
        // Top clientes
        // -----------------------------
        html.append("<div class='card'>")
            .append("<h2>Top Alumnos (por número de compras)</h2>")
            .append("<table>")
            .append("<tr><th>Nombre</th><th>Email</th><th>Total Compras</th></tr>");

        for (ClienteEstadistica c : topClientes) {
            html.append("<tr>")
                .append("<td>").append(c.getNombre()).append("</td>")
                .append("<td>").append(c.getEmail()).append("</td>")
                .append("<td>").append(c.getTotalCompras()).append("</td>")
                .append("</tr>");
        }
        html.append("</table></div>");

        // -----------------------------
        // Tabla completa de ventas
        // -----------------------------
        html.append("<div class='card'>")
            .append("<h2>Listado completo de ventas</h2>")
            .append("<table>")
            .append("<tr>")
            .append("<th>ID</th><th>Nombre</th><th>CIF</th><th>Email</th>")
            .append("<th>Fecha</th><th>Curso</th><th>Precio</th>")
            .append("</tr>");

        for (Venta v : datos) {
            html.append("<tr>")
                .append("<td>").append(v.getId()).append("</td>")
                .append("<td>").append(v.getNombre()).append("</td>")
                .append("<td>").append(v.getCif()).append("</td>")
                .append("<td>").append(v.getEmail()).append("</td>")
                .append("<td>").append(v.getFecha().format(df)).append("</td>")
                .append("<td>").append(v.getProducto()).append("</td>")
                .append("<td>").append(dfNumber.format(v.getPrecio())).append(" €</td>")
                .append("</tr>");
        }
        html.append("</table></div>");

        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Genera el archivo HTML en la ruta indicada.
     *
     * @param rutaArchivo the ruta archivo
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void generarInforme(String rutaArchivo) throws IOException {
        try (FileWriter fw = new FileWriter(rutaArchivo)) {
            fw.write(generarInformeComoString());
        }
    }
}
