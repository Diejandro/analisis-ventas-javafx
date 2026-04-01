package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import models.ClienteEstadistica;
import models.Venta;

/**
 * Clase encargada de procesar los datos de ventas y generar estadísticas.
 */
public class AnalizadorVentas {

	private List<Venta> datos;

	public AnalizadorVentas(List<Venta> datos) {
		this.datos = (datos != null) ? datos : new ArrayList<>();
	}

	/**
	 * Cuenta cuántos clientes únicos existen (basado en CIF).
	 */
	public int obtenerTotalClientes() {
		if( datos.isEmpty()) return 0;
		
		return (int) datos.stream()
				.map(Venta::getCif)
				.distinct()
				.count();
	}

	/**
	 * Suma total de la facturación.
	 */
	public double total(String columna) {
		if(datos.isEmpty()) return 0;
		
		if(columna.equalsIgnoreCase("Precio")) {
			double suma = datos.stream()
					.mapToDouble(Venta::getPrecio)
					.sum();
			return redondear(suma);
		}
		
		return 0;
	}
	
	public int obtenerTotalVentas() {
		return datos.size();
	}

	/**
	 * Gasto promedio por cliente único.
	 */
	public double promedio(String columna) {
		int totalClientes = obtenerTotalClientes();
		if(totalClientes == 0) return 0;
		
		double facturacionTotal = total(columna);
		return redondear(facturacionTotal / totalClientes);
	}

	/**
	 * Cuenta cuántas veces se ha vendido un producto específico.
	 */
	public int flujoVentasPorProducto(String nombreProducto) {
		if(datos.isEmpty() || nombreProducto == null) return 0;
		
		return (int) datos.stream()
				.filter(v -> v.getProducto().nombre().equalsIgnoreCase(nombreProducto))
				.count();
	}
	
	/**
	 * Suma la facturación total de un producto específico.
	 */
	public double flujoFacturacionProducto(String nombreProducto) {
		if(datos.isEmpty() || nombreProducto == null) return 0;
		
		double suma = datos.stream()
				.filter(v -> v.getProducto().nombre().equalsIgnoreCase(nombreProducto))
				.mapToDouble(Venta::getPrecio)
				.sum();
		
		return redondear(suma);
	}
	
	/**
	 * Genera el ranking de clientes agrupando por CIF y ordenando de MAYOR a MENOR compras.
	 */
	
	public List<ClienteEstadistica> rankingClientes() {
	    if (datos == null || datos.isEmpty()) return Collections.emptyList();

	    return datos.stream()
	            .collect(Collectors.groupingBy(Venta::getCif)) // Agrupamos por CIF
	            .entrySet().stream()
	            .map(entry -> {
	                Venta muestra = entry.getValue().get(0);
	                return new ClienteEstadistica(
	                        entry.getKey(),
	                        muestra.getNombre(),
	                        muestra.getEmail(),
	                        (long) entry.getValue().size()
	                );
	            })
	            .sorted(Comparator.comparingLong(ClienteEstadistica::totalCompras).reversed())
	            .collect(Collectors.toList());
	}

	/**
	 * Retorna los N mejores clientes.
	 */
	public List<ClienteEstadistica> topClientes(int n) {
	    return rankingClientes().stream()
	            .limit(n)
	            .collect(Collectors.toList());
	}
	
	
	/**
	 * Método utilitario para centralizar el redondeo a 2 decimales.
	 */
	private double redondear(double valor) {
		return BigDecimal.valueOf(valor)
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
	}
}

