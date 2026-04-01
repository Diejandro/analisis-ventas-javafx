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

/**
 * Clase encargada de generar informes en formato HTML a partir de los datos de ventas.
 */
public class GeneradorInformeHTML {

    private AnalizadorVentas av;
    private List<Venta> datos;

    // Configuración de formato de moneda (Europeo: 1.234,56 €)
    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
    private static final DecimalFormat dfNumber = new DecimalFormat("#,##0.00", symbols);

    public GeneradorInformeHTML(List<Venta> datos) {
        this.datos = datos;
        this.av = new AnalizadorVentas(datos);
    }

    /**
     * Genera el contenido HTML del informe.
     */
    public String generarInformeComoString() {
        // Datos generales extraídos del analizador
        int totalVentas = av.obtenerTotalVentas();
        double ingresosTotales = av.total("Precio");
        double precioMedio = totalVentas > 0 ? ingresosTotales / totalVentas : 0;

        // Ranking de los 10 mejores clientes
        List<ClienteEstadistica> topClientes = av.topClientes(10);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang='es'>")
            .append("<head>")
            .append("<meta charset='UTF-8'>")
            .append("<title>Informe de Ventas</title>")
            .append("<style>")
            .append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 40px; background:#f4f7f6; color:#333; }")
            .append(".card { background:white; border-radius:8px; padding:20px; margin-bottom:25px; box-shadow:0 2px 10px rgba(0,0,0,0.1); }")
            .append(".kpi-box { display:flex; gap:40px; margin-top:15px; border-top:1px solid #eee; padding-top:15px; }")
            .append(".kpi { font-size:16px; } .kpi span { font-weight:bold; color:#2c3e50; font-size:20px; display:block; }")
            .append("table { width:100%; border-collapse:collapse; margin-top:15px; background:white; }")
            .append("th, td { border:1px solid #edf2f7; padding:12px; text-align:left; }")
            .append("th { background:#f8fafc; color:#64748b; text-transform:uppercase; font-size:12px; letter-spacing:0.05em; }")
            .append("tr:hover { background:#f1f5f9; }")
            .append("h1, h2 { color:#1a202c; }")
            .append("</style>")
            .append("</head>")
            .append("<body>");

        // --- SECCIÓN: RESUMEN GENERAL ---
        html.append("<div class='card'>")
            .append("<h1>Resumen Ejecutivo de Ventas</h1>")
            .append("<div class='kpi-box'>")
            .append("<div class='kpi'>Ventas Totales<span>").append(totalVentas).append("</span></div>")
            .append("<div class='kpi'>Facturación Bruta<span>").append(dfNumber.format(ingresosTotales)).append(" €</span></div>")
            .append("<div class='kpi'>Ticket Medio<span>").append(dfNumber.format(precioMedio)).append(" €</span></div>")
            .append("</div></div>");

        // --- SECCIÓN: RANKING CLIENTES ---
        html.append("<div class='card'>")
            .append("<h2>Top 10 Alumnos</h2>")
            .append("<table>")
            .append("<thead><tr><th>Nombre</th><th>Email</th><th>Nº Compras</th></tr></thead>")
            .append("<tbody>");

        for (ClienteEstadistica c : topClientes) {
            // CAMBIO: Al ser un Record, accedemos como nombre(), email() y totalCompras()
            html.append("<tr>")
                .append("<td>").append(c.nombre()).append("</td>")
                .append("<td>").append(c.email()).append("</td>")
                .append("<td>").append(c.totalCompras()).append("</td>")
                .append("</tr>");
        }
        html.append("</tbody></table></div>");

        // --- SECCIÓN: LISTADO DETALLADO ---
        html.append("<div class='card'>")
            .append("<h2>Historial de Transacciones</h2>")
            .append("<table>")
            .append("<thead><tr>")
            .append("<th>ID</th><th>Alumno</th><th>CIF</th>")
            .append("<th>Fecha</th><th>Producto</th><th>Importe</th>")
            .append("</tr></thead><tbody>");

        for (Venta v : datos) {
            html.append("<tr>")
                .append("<td>").append(v.getId()).append("</td>")
                .append("<td>").append(v.getNombre()).append("</td>")
                .append("<td>").append(v.getCif()).append("</td>")
                .append("<td>").append(v.getFecha().format(df)).append("</td>")
                // CAMBIO: Accedemos al nombre del Record Producto
                .append("<td>").append(v.getProducto().nombre()).append("</td>")
                .append("<td>").append(dfNumber.format(v.getPrecio())).append(" €</td>")
                .append("</tr>");
        }
        html.append("</tbody></table></div>");

        html.append("</body></html>");
        return html.toString();
    }

    public void generarInforme(String rutaArchivo) throws IOException {
        try (FileWriter fw = new FileWriter(rutaArchivo)) {
            fw.write(generarInformeComoString());
        }
    }
}