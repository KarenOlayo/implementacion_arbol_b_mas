package arbol_b_mas;

import java.util.Scanner;

/*
Programa principal. Mestra un menú en consola para insertar, buscar y eliminar claves en un árbol B+, mostrarlo y configurar el paso a paso.
Solo se ocupa de pedir y validar los datos del usuario; la lógica del árbol está en ArbolBMas.
Los resultados de cada operación se muestran mediante un ObservadorConsola.
 */

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static String leerLinea() {
        return scanner.nextLine().trim();
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = leerLinea();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Debe ingresar un número entero.");
            }
        }
    }

    public static boolean leerSiNo(String pregunta) {
        while (true) {
            System.out.print(pregunta + " (s/n): ");
            String respuesta = leerLinea().toLowerCase();
            if (respuesta.equals("s")) {
                return true;
            }
            if (respuesta.equals("n")) {
                return false;
            }
            System.out.println("Respuesta no válida. Ingrese s o n.");
        }
    }
  
    public static int solicitarOrden() {
        String mensaje = "\nIngrese el orden del árbol B+. Debe ser un número entre 4 y 9: ";
        int orden = leerEntero(mensaje);
        while (orden < 4 || orden > 9) {
            System.out.println("\nEl orden debe estar entre 4 y 9. Intente nuevamente.");
            orden = leerEntero(mensaje);
        }
        return orden;
    }

    public static boolean validarClave(String clave) {
        if (clave.length() < 1 || clave.length() > 15) {
            System.out.println("La clave debe tener entre 1 y 15 caracteres.");
            return false;
        }

        if (!clave.matches("[a-zA-Z0-9]+")) {
            System.out.println("La clave solo puede contener letras y números.");
            return false;
        }
        return true;
    }

    public static String solicitarClave() {

        System.out.print("\nIngrese la clave: ");
        String clave = leerLinea();
        System.out.println();

        while (!validarClave(clave)) {
            System.out.print("\nIngrese la clave: ");
            clave = leerLinea();
            System.out.println();
        }
        return clave.toLowerCase();
    }

    public static void configurarPasoAPaso(ArbolBMas arbol) {

        boolean activar = leerSiNo("\n¿Mostrar el paso a paso de cada operación?");

        if (!activar) {
            arbol.establecerObservador(null);
            System.out.println("Paso a paso desactivado.");
            return;
        }

        boolean mostrarArbol = leerSiNo("¿Mostrar el árbol después de cada cambio?");

        arbol.establecerObservador(new ObservadorConsola(mostrarArbol));
        System.out.println("Paso a paso activado.");
    }

    public static void mostrarMenu() {
        System.out.println("\n--- Árbol B+ ---");
        System.out.println("\n1. Insertar clave");
        System.out.println("2. Buscar clave");
        System.out.println("3. Eliminar clave");
        System.out.println("4. Mostrar árbol");
        System.out.println("5. Configurar paso a paso");
        System.out.println("6. Salir");
    }

    public static int solicitarOpcion() {
        String mensaje = "\nSeleccione una opción: ";
        int opcion = leerEntero(mensaje);

        while (opcion < 1 || opcion > 6) {
            System.out.println("\nOpción no válida. Debe ser un número entre 1 y 6.");
            opcion = leerEntero(mensaje);
        }
        return opcion;
    }

    public static void main(String[] args) {
        int orden = solicitarOrden();
        ArbolBMas arbol = new ArbolBMas(orden);
        arbol.establecerObservador(new ObservadorConsola(true));

        boolean salir = false;
        
        while (!salir) {
            mostrarMenu();
            int opcion = solicitarOpcion();

            switch (opcion) {
                case 1:
                    String claveInsertar = solicitarClave();
                    arbol.insertar(claveInsertar);
                    break;
                case 2:
                    String claveBuscar = solicitarClave();
                    arbol.buscar(claveBuscar);
                    break;
                case 3:
                    String claveEliminar = solicitarClave();
                    arbol.eliminar(claveEliminar);
                    break;
                case 4:
                    if (arbol.estaVacio()) {
                        System.out.println("\nEl árbol está vacío.");
                    } else {
                        System.out.println("\n" + arbol.comoTexto());
                        System.out.println("\nHojas enlazadas: " + arbol.clavesEnOrden());
                        System.out.println();
                        arbol.imprimir();
                    }
                    break;
                case 5:
                    configurarPasoAPaso(arbol);
                    break;
                case 6:
                    salir = true;
                    break;
            }
        }
    }
}
