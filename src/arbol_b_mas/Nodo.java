package arbol_b_mas;

import java.util.ArrayList;
import java.util.List;

/*
Nodo del árbol B+ que pude ser una hoja o nodo interno.
En una hoja, las claves corresponden a los datos reales, y en un nodo interno, son separadores.
Solo los nodos hojas tienen enlaces al nodo anterior y al nodo siguiente.
*/

public class Nodo {
    
    List<String> claves;
    List<Nodo> hijos;
    boolean esHoja;

    Nodo hojaSiguiente;
    Nodo hojaAnterior;

    public Nodo(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.hojaAnterior = null;
        this.hojaSiguiente = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < claves.size(); i++) {
            sb.append(claves.get(i));
            if (i < claves.size() - 1)
                sb.append(" | ");
        }
        sb.append("]");
        return sb.toString();
    }
}