package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.ClienteEstadistica;
import models.Venta;

// TODO: Auto-generated Javadoc
/**
 * The Class AnalizadorVentas.
 */
public class AnalizadorVentas {

	/** The datos. */
	private List<Venta> datos;

	/**
	 * Instantiates a new analizador ventas.
	 *
	 * @param datos the datos
	 */
	public AnalizadorVentas(List<Venta> datos) {
		this.datos = datos;
	}

	/**
	 * Obtener total clientes.
	 *
	 * @return the int
	 */
	public int obtenerTotalClientes() {
		
		return datos == null ? 0 : new HashSet<>(datos).size();
	}

	/**
	 * Total.
	 *
	 * @param columna the columna
	 * @return the double
	 */
	public double total(String columna) {
		if (datos == null||datos.isEmpty())
			return 0;
		if (columna.equalsIgnoreCase("Precio")) {
			double suma = datos.stream()
					.mapToDouble(Venta::getPrecio)
					.sum();
			
			BigDecimal bd = BigDecimal.valueOf(suma);
			bd = bd.setScale(2, RoundingMode.HALF_UP); // redondeo a 2 decimales
			return bd.doubleValue();
		}

		return 0;
	}
	
	/**
	 * Obtener total ventas.
	 *
	 * @return the int
	 */
	public int obtenerTotalVentas() {
		return datos != null ? datos.size(): 0;
	}

	/**
	 * Promedio.
	 *
	 * @param columna the columna
	 * @return the double
	 */
	public double promedio(String columna) {
		int clientes = obtenerTotalClientes();
		if(clientes == 0) return 0;
		double facturacionTotal = total(columna);

		double promedioPorCliente = facturacionTotal / clientes;
		
		BigDecimal bd = BigDecimal.valueOf(promedioPorCliente);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		
		return bd.doubleValue();
	}

	/**
	 * Flujo ventas por producto.
	 *
	 * @param productos the productos
	 * @return the int
	 */
	public int flujoVentasPorProducto(String productos) {
		
		if (datos == null||datos.isEmpty())
			return 0;

		int contador = 0;

		for (Venta v : datos) {
			if (v.getProducto().equalsIgnoreCase(productos)) {
				contador++;
			}
		}
		return contador;
	}
	
	/**
	 * Flujo facturacion producto.
	 *
	 * @param productos the productos
	 * @return the double
	 */
	public double flujoFacturacionProducto(String productos) {
		if(datos == null||datos.isEmpty()) return 0;
		
		List<Venta> lista = new ArrayList<>();
		
		for(Venta v: datos) {
			if(v.getProducto().equalsIgnoreCase(productos)) {
				lista.add(v);
			}
		}
		
		double suma = lista.stream()
				.mapToDouble(Venta::getPrecio)
				.sum();
		
		BigDecimal bd = BigDecimal.valueOf(suma);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		
		return bd.doubleValue();
	}
	
	/**
	 * Ranking clientes.
	 *
	 * @return the list
	 */
	// Retorna todos los clientes ordenados por número de compras (descendente)
	public List<ClienteEstadistica> rankingClientes() {
	    if (datos == null || datos.isEmpty()) return Collections.emptyList();

	    // Agrupar por CIF
	    Map<String, List<Venta>> agrupado = datos.stream()
	            .collect(Collectors.groupingBy(Venta::getCif));

	    // Convertir a lista de ClienteEstadistica
	    List<ClienteEstadistica> ranking = agrupado.entrySet().stream()
	            .map(entry -> {
	                Venta v = entry.getValue().get(0); // Tomamos nombre y email del primer registro
	                return new ClienteEstadistica(
	                        v.getNombre(),
	                        v.getEmail(),
	                        entry.getValue().size()
	                );
	            })
	            .sorted(Comparator.comparingLong(ClienteEstadistica::getTotalCompras).reversed())
	            .toList();

	    return ranking;
	}

	/**
	 * Top clientes.
	 *
	 * @param n the n
	 * @return the list
	 */
	// Retorna solo los N clientes con más compras
	public List<ClienteEstadistica> topClientes(int n) {
	    return rankingClientes().stream()
	            .limit(n)
	            .toList();
	}
	
}
















