package models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa una transacción de venta.
 * Se mantiene como Clase para permitir mutabilidad y futuras extensiones.
 */
public class Venta {
	
	private Long id;
	private Alumno alumno;
	private LocalDate fecha;
	private Producto producto;
	
	private double precio;
	
	public Venta() {}

	/**
	 * Constructor actualizado para integrar el objeto Producto.
	 */
	public Venta(Long id, Alumno alumno, Producto producto, double precio, LocalDate fecha){
		this.id = id;
		this.alumno = alumno;
		this.fecha = fecha;
		this.producto = producto;
		this.precio = precio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	// Métodos puente para mantener la compatibilidad con las tablas de la interfaz
	public String getNombre() {
		return (alumno != null) ? alumno.getNombre() : "";
	}
	
	public String getCif() {
		return (alumno != null) ? alumno.getCIF() : "";
	}
	
	public String getEmail() {
		return (alumno != null) ? alumno.getEmail() : "";
	}

	public Alumno getAlumno() {
		return alumno;
	}

	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	@Override
	public String toString() {
		// Usamos producto.nombre() para el log, ya que producto es un Record
		return "Venta [id=" + id + 
               ", cif=" + getCif() + 
               ", producto=" + (producto != null ? producto.nombre() : "null") + 
               ", precio=" + precio + 
               ", fecha=" + fecha + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Venta other = (Venta) obj;
		return Objects.equals(id, other.id);
	}
}