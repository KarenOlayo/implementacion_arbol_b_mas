package arbol_b_mas;

/*
Interfaz para recibir los mensajes del paso a paso de las operaciones del árbol.
La clase ArbolBMas envía sus mensajes a esta interfaz sin saber qué se hace con ellos, así que se pueden tratar a conveniencia.
En este caso en particular, se mostrarán por consola.
*/

public interface ObservadorPasos {

    /* Recibe la descripción de un paso de la operación en curso. */
    void paso(String descripcion);

    /* Recibe el dibujo del árbol como texto. */
    default void arbol(String representacion) {}
}