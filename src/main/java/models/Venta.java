package models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * The Class Venta.
 */
public class Venta {
	
	/** The id. */
	private Long id;
	
	private Alumno alumno;
	
	/** The fecha. */
	private LocalDate fecha;
	
	/** The producto. */
	private String producto;
	
	/** The precio. */
	private double precio;
	
	/**
	 * Instantiates a new venta.
	 */
	public Venta() {}

	/**
	 * Instantiates a new venta.
	 *
	 * @param id the id
	 * @param nombre the nombre
	 * @param cif the cif
	 * @param email the email
	 * @param producto the producto
	 * @param precio the precio
	 * @param fecha the fecha
	 */
	public Venta(Long id, Alumno alumno, String producto, double precio, LocalDate fecha){
		super();
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
	
	public String getNombre() {
		return alumno.getNombre();
	}
	
	public String getCif() {
		return alumno.getCIF();
	}
	
	public String getEmail() {
		return alumno.getEmail();
	}


	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}


	public String getProducto() {
		return producto;
	}


	public void setProducto(String producto) {
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
		return "VentasBeans [id=" + id + ", nombre=" + alumno.getNombre() + ", cif=" + alumno.getCIF() + ", email=" + alumno.getEmail() + ", fecha=" + fecha
				+ ", producto=" + producto + ", precio=" + precio + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Venta other = (Venta) obj;
		return Objects.equals(id, other.id);
	}
}


