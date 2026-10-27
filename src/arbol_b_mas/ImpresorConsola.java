package arbol_b_mas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Dibuja el árbol como texto en consola. Cada nodo se muestra con el texto que devuelve su
toString(), y se une a sus hijos con líneas rectas.
*/

public class ImpresorConsola {

    private static final int SEPARACION = 3;

    private final boolean usarUnicode;

    public ImpresorConsola() {
        this(true);
    }

    public ImpresorConsola(boolean usarUnicode) {
        this.usarUnicode = usarUnicode;
    }

    public String generar(Nodo raiz) {

        if (raiz == null) {
            return "(árbol vacío)";
        }

        int niveles = calcularNiveles(raiz);
        int anchoTotal = calcularAncho(raiz);

        char[][] lienzo = new char[2 * niveles - 1][anchoTotal];
        for (char[] fila : lienzo) {
            Arrays.fill(fila, ' ');
        }

        dibujar(raiz, 0, 0, lienzo);

        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < lienzo.length; i++) {
            if (i > 0) {
                texto.append("\n");
            }
            texto.append(new String(lienzo[i]).replaceAll("\\s+$", ""));
        }
        return texto.toString();
    }

    private int calcularNiveles(Nodo nodo) {
        if (nodo.hijos.isEmpty()) {
            return 1;
        }
        return 1 + calcularNiveles(nodo.hijos.get(0));
    }

    private int anchoDeHijos(Nodo nodo) {
        int suma = 0;
        for (Nodo hijo : nodo.hijos) {
            suma += calcularAncho(hijo);
        }
        return suma + SEPARACION * (nodo.hijos.size() - 1);
    }

    private int calcularAncho(Nodo nodo) {
        int anchoEtiqueta = nodo.toString().length();
        if (nodo.hijos.isEmpty()) {
            return anchoEtiqueta;
        }
        return Math.max(anchoEtiqueta, anchoDeHijos(nodo));
    }

    private int dibujar(Nodo nodo, int nivel, int izquierda, char[][] lienzo) {

        String etiqueta = nodo.toString();
        int anchoSubarbol = calcularAncho(nodo);
        int inicio = izquierda + (anchoSubarbol - etiqueta.length()) / 2;
        int centro = inicio + etiqueta.length() / 2;

        for (int i = 0; i < etiqueta.length(); i++) {
            lienzo[nivel * 2][inicio + i] = etiqueta.charAt(i);
        }

        if (!nodo.hijos.isEmpty()) {
            int posicion = izquierda + (anchoSubarbol - anchoDeHijos(nodo)) / 2;
            List<Integer> centros = new ArrayList<>();

            for (Nodo hijo : nodo.hijos) {
                centros.add(dibujar(hijo, nivel + 1, posicion, lienzo));
                posicion += calcularAncho(hijo) + SEPARACION;
            }
            dibujarConectores(lienzo, nivel * 2 + 1, centro, centros);
        }
        return centro;
    }

    private void dibujarConectores(char[][] lienzo, int fila, int centroPadre, List<Integer> centrosHijos) {

        int primero = centrosHijos.get(0);
        int ultimo = centrosHijos.get(centrosHijos.size() - 1);
        int desde = Math.min(centroPadre, primero);
        int hasta = Math.max(centroPadre, ultimo);

        for (int x = desde; x <= hasta; x++) {
            boolean arriba = (x == centroPadre);
            boolean abajo = centrosHijos.contains(x);
            boolean izquierda = (x > desde);
            boolean derecha = (x < hasta);
            lienzo[fila][x] = conector(arriba, abajo, izquierda, derecha);
        }
    }

    private char conector(boolean arriba, boolean abajo, boolean izquierda, boolean derecha) {

        if (!usarUnicode) {
            if (izquierda || derecha) {
                if (arriba || abajo) {
                    return '+';
                }
                return '-';
            }
            return '|';
        }

        if (arriba && abajo) {
            if (izquierda && derecha) {
                return '\u253C';
            }
            if (izquierda) {
                return '\u2524';
            }
            if (derecha) {
                return '\u251C';
            }
            return '\u2502';
        }

        if (arriba) {
            if (izquierda && derecha) {
                return '\u2534';
            }
            if (izquierda) {
                return '\u2518';
            }
            if (derecha) {
                return '\u2514';
            }
            return '\u2502';
        }

        if (abajo) {
            if (izquierda && derecha) {
                return '\u252C';
            }
            if (izquierda) {
                return '\u2510';
            }
            if (derecha) {
                return '\u250C';
            }
            return '\u2502';
        }

        return '\u2500';
    }
}