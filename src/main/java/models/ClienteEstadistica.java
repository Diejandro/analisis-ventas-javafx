package models;

/**
 * The Class ClienteEstadistica.
 */
public class ClienteEstadistica {
    
    /** The nombre. */
    private String nombre;
    
    /** The email. */
    private String email;
    
    /** The total compras. */
    private int totalCompras;

    /**
     * Instantiates a new cliente estadistica.
     *
     * @param nombre the nombre
     * @param email the email
     * @param totalCompras the total compras
     */
    public ClienteEstadistica(String nombre, String email, int totalCompras) {
        this.nombre = nombre;
        this.email = email;
        this.totalCompras = totalCompras;
    }

    /**
     * Gets the nombre.
     *
     * @return the nombre
     */
    public String getNombre() { return nombre; }
    
    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() { return email; }
    
    /**
     * Gets the total compras.
     *
     * @return the total compras
     */
    public long getTotalCompras() { return totalCompras; }
}

