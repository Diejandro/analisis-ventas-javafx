package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Alumno;
import models.Producto;
import models.Venta;
import services.AnalizadorVentas;

public class AnalizadorVentasTest {
	
	private AnalizadorVentas analizador;

	@BeforeEach
	void setUp() {
		Alumno alumno1 = new Alumno(1L, "12345678A", "Juan", "juan@test.com");
		Alumno alumno2 = new Alumno(2L, "87654321B", "Maria", "maria@test.com");
		
		List<Venta> ventasPruebas = Arrays.asList(
				new Venta(101L, alumno1, new Producto("Curso Básico", "Curso", 295.0), 295.0, LocalDate.now()),
	            new Venta(102L, alumno1, new Producto("Curso Intermedio", "Curso", 495.0), 495.0, LocalDate.now()),
	            new Venta(103L, alumno2, new Producto("Curso Básico", "Curso", 295.0), 295.0, LocalDate.now())
				);
		
		analizador = new AnalizadorVentas(ventasPruebas);
	}
	
	@Test
	void testObtenerTotalClientes()	{
		//Juan ha comprado dos veces, María una. Total clientes únicos: 2.
		assertEquals(2, analizador.obtenerTotalClientes(), "El conteo de clientes únicos debería ser 2");
	}
	
	@Test
	void testTotalFacturacion() {
		//295 + 495 + 295 = 1085
		assertEquals(1085.0, analizador.total("Precio"), 0.001);
	}
	
	@Test
	void testFlujoVentasPorProducto() {
		assertEquals(2, analizador.flujoVentasPorProducto("Curso Básico"));
		assertEquals(1, analizador.flujoVentasPorProducto("Curso Intermedio"));
	}
	
	@Test
	void testRankingClientes() {
		var ranking = analizador.rankingClientes();
		assertFalse(ranking.isEmpty());
		//El primero debería ser Juan (CIF 12345678A) con  compras
		assertEquals("12345678A", ranking.get(0).cif());
		assertEquals(2, ranking.get(0).totalCompras());
	}
}











