package arbol_b_mas;

import java.util.ArrayList;
import java.util.List;

public class ImpresorConsola {

    public String generar(Nodo raiz) {

        if (raiz == null) {
            return "(árbol vacío)";
        }

        StringBuilder texto = new StringBuilder();
        List<Nodo> nivelActual = new ArrayList<>();
        nivelActual.add(raiz);
        int numeroNivel = 0;

        while (!nivelActual.isEmpty()) {

            if (numeroNivel > 0) {
                texto.append("\n");
            }
            texto.append("Nivel ").append(numeroNivel).append(": ");

            List<Nodo> nivelSiguiente = new ArrayList<>();

            for (int i = 0; i < nivelActual.size(); i++) {
                Nodo nodo = nivelActual.get(i);
                texto.append(nodo);
                if (i < nivelActual.size() - 1) {
                    texto.append("  ");
                }
                nivelSiguiente.addAll(nodo.hijos);
            }

            nivelActual = nivelSiguiente;
            numeroNivel++;
        }
        return texto.toString();
    }
}