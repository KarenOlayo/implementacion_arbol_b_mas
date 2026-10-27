package arbol_b_mas;

/* Observador que muestra en la consola los mensajes del paso a paso y, si se activa, el dibujo del árbol después de cada cambio. */

public class ObservadorConsola implements ObservadorPasos {
    
    /* Si es true, también se imprime el dibujo del árbol; si no, solo los mensajes. */
    private final boolean mostrarArbol;

    /** Crea el observador indicando si debe mostrar el dibujo del árbol. */
    public ObservadorConsola(boolean mostrarArbol) {
        this.mostrarArbol = mostrarArbol;
    }
    
    /* Imprime el mensaje con el prefijo "  > ". */
    @Override
    public void paso(String descripcion) {
        System.out.println("  > " + descripcion);
    }

    /** Imprime el dibujo del árbol con sangría, solo si mostrarArbol es true. */
    @Override
    public void arbol(String representacion) {
        if (mostrarArbol) {
            System.out.println();
            System.out.println("    " + representacion.replace("\n", "\n    "));
            System.out.println();
        }
    }
}