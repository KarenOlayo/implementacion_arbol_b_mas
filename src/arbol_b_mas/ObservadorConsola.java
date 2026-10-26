package arbol_b_mas;

public class ObservadorConsola implements ObservadorPasos {

    private final boolean mostrarArbol;

    public ObservadorConsola(boolean mostrarArbol) {
        this.mostrarArbol = mostrarArbol;
    }

    @Override
    public void paso(String descripcion) {
        System.out.println("  > " + descripcion);
    }

    @Override
    public void arbol(String representacion) {
        if (mostrarArbol) {
            System.out.println();
            System.out.println("    " + representacion.replace("\n", "\n    "));
            System.out.println();
        }
    }
}