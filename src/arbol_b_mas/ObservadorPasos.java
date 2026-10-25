package arbol_b_mas;

public interface ObservadorPasos {
    void paso(String descripcion);
    default void arbol(String representacion) {}
}