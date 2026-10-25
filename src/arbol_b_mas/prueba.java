package arbol_b_mas;

import java.util.ArrayList;

public class prueba {
    
    public String calcular(int orden){
    
        int minClavesInterno = (int) Math.ceil((orden/2.0) - 1);
        int minClavesHoja = (int) Math.ceil((orden-1) / 2.0);

        return "Clave: " + orden + ", Min Claves Interno: " + minClavesInterno + ", Min Claves Hoja: " + minClavesHoja;
    }

    private int comparar(String clave1, String clave2) {
        return clave1.compareToIgnoreCase(clave2);
    }

    private int getClave(ArrayList<Integer> claves, int posicion) {
        return claves.get(posicion);
    }

    public static void main(String[] args) {
        prueba p = new prueba();
        for (int i = 4; i <= 9; i++) {
            System.out.println(p.calcular(i));
        }

        System.out.println("\nComparación de claves:");
        String clave1 = "70";
        String clave2 = "50";
        int resultado = p.comparar(clave1, clave2);
        System.out.println("Resultado de la comparación: " + resultado);

        ArrayList<Integer> claves = new ArrayList<>();
        claves.add(10);
        claves.add(20);

        int clave = p.getClave(claves, 2);
        System.out.println("Clave en 2: " + clave);
    }
}

