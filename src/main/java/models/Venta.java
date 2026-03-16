package models;

import java.time.LocalDate;

/**
 * The Class Venta.
 */
public class Venta {
	
	/** The id. */
	private String id;
	
	/** The nombre. */
	private String nombre;
	
	/** The cif. */
	private String cif;
	
	/** The email. */
	private String email;
	
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
	public Venta(String id, String nombre, String cif, String email, String producto, double precio, LocalDate fecha){
		super();
		this.id = id;
		this.nombre = nombre;
		this.cif = cif;
		this.email = email;
		this.fecha = fecha;
		this.producto = producto;
		this.precio = precio;
	}

	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the nombre.
	 *
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Sets the nombre.
	 *
	 * @param nombre the new nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Gets the cif.
	 *
	 * @return the cif
	 */
	public String getCif() {
		return cif;
	}

	/**
	 * Sets the cif.
	 *
	 * @param cif the new cif
	 */
	public void setCif(String cif) {
		this.cif = cif;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the fecha.
	 *
	 * @return the fecha
	 */
	public LocalDate getFecha() {
		return fecha;
	}

	/**
	 * Sets the fecha.
	 *
	 * @param fecha the new fecha
	 */
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/**
	 * Gets the producto.
	 *
	 * @return the producto
	 */
	public String getProducto() {
		return producto;
	}

	/**
	 * Sets the producto.
	 *
	 * @param producto the new producto
	 */
	public void setProducto(String producto) {
		this.producto = producto;
	}

	/**
	 * Gets the precio.
	 *
	 * @return the precio
	 */
	public Double getPrecio() {
		return precio;
	}

	/**
	 * Sets the precio.
	 *
	 * @param precio the new precio
	 */
	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "VentasBeans [id=" + id + ", nombre=" + nombre + ", cif=" + cif + ", email=" + email + ", fecha=" + fecha
				+ ", producto=" + producto + ", precio=" + precio + "]";
	}
	
	/**
	 * Equals.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venta)) return false;
        Venta v = (Venta) o;
        return cif.equalsIgnoreCase(v.cif);
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return cif.toLowerCase().hashCode();
    }
}


