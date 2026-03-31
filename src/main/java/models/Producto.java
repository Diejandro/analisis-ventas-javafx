package models;

/**
 * Representa un producto del catálogo (Curso, Taller, etc.)
 * * @param nombre Nombre descriptivo del producto.
 * @param tipo Categoría (por defecto "Curso", extensible en el futuro).
 * @param precioSugerido El precio "oficial" que usaremos para autocompletar en la interfaz.
 */

public record Producto(String nombre, String tipo, double precioSugerido) {
	
	/**
     * Constructor compacto para cuando creamos un producto solo con el nombre
     * (útil cuando estamos procesando el CSV y aún no sabemos el precio oficial).
     */
	public Producto(String nombre) {
		this(nombre, "Curso", 0.0);
	}
	
	/**
     * Vital para que las tablas de la interfaz muestren el nombre 
     * directamente sin configuraciones extra.
     */
	@Override
	public String toString()	{
		return nombre;
	}
}
