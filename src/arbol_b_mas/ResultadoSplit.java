package arbol_b_mas;

public class ResultadoSplit {
    
    final String clavePromovida;
    final Nodo nodoIzquierdo, nodoDerecho;

    ResultadoSplit(String clavePromovida, Nodo nodoIzquierdo, Nodo nodoDerecho) {
        this.clavePromovida = clavePromovida;
        this.nodoIzquierdo = nodoIzquierdo;
        this.nodoDerecho = nodoDerecho;
    }
}
