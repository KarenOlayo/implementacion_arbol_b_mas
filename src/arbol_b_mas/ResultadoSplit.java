package arbol_b_mas;

/*
Resultado de la división de un nodo cuando ocurre un overflow.
Agrupa la clave promovida, el nodo que conserva la parte izquierda y el nodo nuevo que recibe la parte derecha.
Si la división es de hoja, la clave promovida se copia al padre, si es de un nodo interno, se traslada.
Si o hay división, el resultado del split es null.
*/

public class ResultadoSplit {
    
    final String clavePromovida;
    final Nodo nodoIzquierdo, nodoDerecho;

    ResultadoSplit(String clavePromovida, Nodo nodoIzquierdo, Nodo nodoDerecho) {
        this.clavePromovida = clavePromovida;
        this.nodoIzquierdo = nodoIzquierdo;
        this.nodoDerecho = nodoDerecho;
    }
}
