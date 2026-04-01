package models;

/**
 * Record para las estadísticas de cliente.
 * Inmutable, limpio y perfecto para el ranking.
 */
public record ClienteEstadistica(String cif, String nombre, String email, long totalCompras) {
    
    /**
     * Este método permite que las medallas (Diamante, Platino, etc.) 
     * sigan funcionando si tu lógica depende de "getTotalCompras()".
     */
    public long getTotalCompras() {
        return totalCompras;
    }
}

