package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The Class RegistroCSV.
 */
public class RegistroCSV {

    private String id;
    private String nombre;
    private String cif;
    private String email;
    private Producto producto;
    private double precio;
    private LocalDate fecha;

    /** The Constant FORMATOS_FECHA. */
    private static final DateTimeFormatter[] FORMATOS_FECHA = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    };

    /**
     * Instantiates a new registro CSV.
     */
    public RegistroCSV() {}

    /**
     * Instantiates a new registro CSV.
     *
     * @param id the id
     * @param nombre the nombre
     * @param cif the cif
     * @param email the email
     * @param producto the producto
     * @param precio the precio
     * @param fecha the fecha
     */
    public RegistroCSV(String cif, String nombre, String email, String id,
                       Producto producto, double precio, LocalDate fecha) {
        this.id = id;
        this.nombre = nombre;
        this.cif = cif;
        this.email = email;
        this.producto = producto;
        this.precio = precio;
        this.fecha = fecha;
    }

    /*
     * Convierte un objeto Venta en un registroCSV para poder guardarlo.
     */
    public static RegistroCSV fromVenta(Venta v) {
        return new RegistroCSV(
                v.getAlumno().getCIF(),
                v.getAlumno().getNombre(),
                v.getAlumno().getEmail(),
                v.getId().toString(),
                v.getProducto(),
                v.getPrecio(),
                v.getFecha()
        );
    }

    /**
     * Parsear desde array de String.
     *
     * @param campos the campos
     * @return the registro CSV
     */
    public static RegistroCSV fromArray(String[] campos) {
    	
    	if (campos == null || campos.length < 7) {
    		return null;
    	}
    	
    	RegistroCSV r = new RegistroCSV();
    	
    	r.setCif(campos[0].trim());
    	r.setNombre(campos[1].trim());
    	r.setEmail(campos[2].trim());
    	r.setId(campos[3].trim());
    	r.setProducto(new Producto(campos[4].trim()));
    	
    	r.setPrecio(parseDouble(campos[5]));
    	r.setFecha(parseFecha(campos[6]));
    	
    	return r;
    }

    /**
     * Convertir a CSV según separador
     *
     * @param sep the sep
     * @return the string
     */
    public String toCSV(char sep) {
        String s = String.valueOf(sep);
        return String.join(s,
                cif != null ? cif : "",
                nombre != null ? nombre : "",
                email != null ? email : "",   
                id != null ? id : "",         
                producto != null ? producto.nombre() : "", 
                String.valueOf(precio),       
                fecha != null ? fecha.toString() : "" 
        );
    }

    /**
     * Parseos auxiliares
     *
     * @param valor the valor
     * @return the double
     */
    private static double parseDouble(String valor) {
        if (valor == null || valor.isBlank()) return 0.0;
        try {
            String limpio = valor.trim().replaceAll("[^0-9.,\\-]", "").replace(',', '.');
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Parses the fecha.
     *
     * @param valor the valor
     * @return the local date
     */
    private static LocalDate parseFecha(String valor) {
        if (valor == null || valor.isBlank()) return LocalDate.now();
        String limpio = valor.trim();
        for (DateTimeFormatter f : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(limpio, f);
            } catch (DateTimeParseException e) {
                // siguiente formato
            }
        }
        return LocalDate.now();
    }


    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() { return id; }
    
    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the nombre.
     *
     * @return the nombre
     */
    public String getNombre() { return nombre; }
    
    /**
     * Sets the nombre.
     *
     * @param nombre the new nombre
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Gets the cif.
     *
     * @return the cif
     */
    public String getCif() { return cif; }
    
    /**
     * Sets the cif.
     *
     * @param cif the new cif
     */
    public void setCif(String cif) { this.cif = cif; }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() { return email; }
    
    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the producto.
     *
     * @return the producto
     */
    public Producto getProducto() { return producto; }
    
    /**
     * Sets the producto.
     *
     * @param curso the new producto
     */
    public void setProducto(Producto producto) { this.producto = producto; }

    /**
     * Gets the precio.
     *
     * @return the precio
     */
    public double getPrecio() { return precio; }
    
    /**
     * Sets the precio.
     *
     * @param precio the new precio
     */
    public void setPrecio(double precio) { this.precio = precio; }

    /**
     * Gets the fecha.
     *
     * @return the fecha
     */
    public LocalDate getFecha() { return fecha; }
    
    /**
     * Sets the fecha.
     *
     * @param fecha the new fecha
     */
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}


