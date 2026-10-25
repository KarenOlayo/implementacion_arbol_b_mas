package arbol_b_mas;

public class ArbolBMas {

    private Nodo raiz;

    private final int orden;
    private final int maxClaves;
    private final int minClavesInterno;
    private final int minClavesHoja;

    private final ImpresorImagen impresor = new ImpresorImagen();
    private final ImpresorConsola impresorConsola = new ImpresorConsola();
    private ObservadorPasos observador = null;
    

    public ArbolBMas(int orden) {
        
        this.raiz = null;

        this.orden = orden;
        this.maxClaves = orden - 1;
        this.minClavesInterno = (int) Math.ceil((orden/2.0) - 1);
        this.minClavesHoja = (int) Math.ceil((orden - 1) / 2.0);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    private int comparar(String clave1, String clave2) {
        return clave1.compareToIgnoreCase(clave2);
    }

    private int indiceHijo(Nodo nodo, String clave) {

        int i = 0;
        while (i < nodo.claves.size() && comparar(clave, nodo.claves.get(i)) >= 0) {
            i++;
        }
        return i;
    }

    private int posicionEnHoja(Nodo hoja, String clave) {
    
        int i = 0;
        while (i < hoja.claves.size() && comparar(clave, hoja.claves.get(i)) > 0) {
            i++;
        }
        return i;
    }

    // 1. Insertar

    private ResultadoSplit dividirHoja(Nodo hoja) {
        
        int total = hoja.claves.size();
        int mitad = total / 2;

        Nodo derecho = new Nodo(true);
        derecho.claves.addAll(hoja.claves.subList(mitad, total));
        hoja.claves.subList(mitad, total).clear();

        derecho.hojaSiguiente = hoja.hojaSiguiente; // Puntero al siguiente nodo hoja
        derecho.hojaAnterior = hoja; // Puntero al nodo hoja anterior

        if (hoja.hojaSiguiente != null) {
            hoja.hojaSiguiente.hojaAnterior = derecho;
        }
        hoja.hojaSiguiente = derecho;

        String clavePromovida = derecho.claves.get(0);
        notificar("División de hoja: quedan " + hoja + " y " + derecho + ". La clave '" + clavePromovida + "' se copia al nodo padre.");
        return new ResultadoSplit(clavePromovida, hoja, derecho);
    }

    private ResultadoSplit dividirNodoInterno(Nodo nodo) {
        
        int total = nodo.claves.size();
        int mitad = (total - 1) / 2;
        String clavePromovida = nodo.claves.get(mitad);

        Nodo derecho = new Nodo(false);
        derecho.claves.addAll(nodo.claves.subList(mitad + 1, total));
        derecho.hijos.addAll(nodo.hijos.subList(mitad + 1, nodo.hijos.size()));

        nodo.claves.subList(mitad, total).clear();
        nodo.hijos.subList(mitad + 1, nodo.hijos.size()).clear();

        notificar("División de nodo interno: quedan " + nodo + " y " + derecho
            + ". La clave '" + clavePromovida + "' sube al nodo padre y deja de estar en estos nodos.");

        return new ResultadoSplit(clavePromovida, nodo, derecho);
    }

    public boolean insertar(String clave){
         
        if (raiz == null) {
            notificar("El árbol está vacío. Se crea una hoja raíz con la clave '" + clave + "'.");
            raiz = new Nodo(true);
            raiz.claves.add(clave);
            notificarArbol();
            notificar("Operación finalizada: la clave '" + clave + "' fue insertada.");
            return true;
        }

        notificar("Se inicia la inserción de '" + clave + "' en la raíz " + raiz + ".");

        EstadoInsercion estado = new EstadoInsercion();
        ResultadoSplit split = insertarRecursivo(raiz, clave, estado);

        if (!estado.insertado) {
            notificar("Operación finalizada: el árbol no se modificó porque la clave '" + clave + "' ya existe.");
            return false;
        }

        if (split != null) {
            notificar("La raíz se dividió. Se crea una nueva raíz con la clave '"
                + split.clavePromovida + "' y los hijos " + split.nodoIzquierdo
                + " y " + split.nodoDerecho + ".");
            Nodo nuevaRaiz = new Nodo(false);
            nuevaRaiz.claves.add(split.clavePromovida);
            nuevaRaiz.hijos.add(split.nodoIzquierdo);
            nuevaRaiz.hijos.add(split.nodoDerecho);
            raiz = nuevaRaiz;
            notificarArbol();
        }
        notificar("Operación finalizada: la clave '" + clave + "' fue insertada.");
        return true;
    }

    private ResultadoSplit insertarRecursivo(Nodo nodo, String clave, EstadoInsercion estado){
        
        if (nodo.esHoja) {

            int posicion = posicionEnHoja(nodo, clave);
            notificar("Se llegó a la hoja " + nodo + ". La posición de inserción es " + posicion + ".");

            if (posicion < nodo.claves.size() && comparar(nodo.claves.get(posicion), clave) == 0) {
                estado.insertado = false;
                notificar("La clave '" + clave + "' ya existe en la hoja. No se inserta.");
                return null;
            }

            nodo.claves.add(posicion, clave);
            estado.insertado = true;
            notificar("Se inserta '" + clave + "'. La hoja queda " + nodo + ".");
            notificarArbol();

            if (nodo.claves.size() > maxClaves) {
                notificar("La hoja tiene " + nodo.claves.size() + " claves y el máximo es "
                    + maxClaves + ". Se divide.");
                return dividirHoja(nodo);
            }
            notificar("La hoja no excede el máximo de " + maxClaves + " claves. No se divide.");
            return null;
        }

        int indiceDescenso = indiceHijo(nodo, clave);
        notificar("En el nodo interno " + nodo + " la clave '" + clave + "' corresponde al hijo de índice " + indiceDescenso + ".");
        ResultadoSplit splitHijo = insertarRecursivo(nodo.hijos.get(indiceDescenso), clave, estado);
        
        if (!estado.insertado || splitHijo == null) {
            return null;
        }

        notificar("El hijo de índice " + indiceDescenso + " se dividió. La clave '"
            + splitHijo.clavePromovida + "' se inserta en el nodo interno " + nodo + ".");

        nodo.claves.add(indiceDescenso, splitHijo.clavePromovida);
        nodo.hijos.add(indiceDescenso + 1, splitHijo.nodoDerecho);
        notificar("El nodo interno queda " + nodo + ".");
        notificarArbol();

        if (nodo.claves.size() > maxClaves) {
            notificar("El nodo interno tiene " + nodo.claves.size() + " claves y el máximo es " + maxClaves + ". Se divide.");
            return dividirNodoInterno(nodo);
        }
        notificar("El nodo interno no excede el máximo de " + maxClaves + " claves. No se divide.");
        return null;
    }
    
    // 2. Buscar
    
    public boolean buscar(String clave) {

        if (estaVacio()) {
            notificar("El árbol está vacío, la clave '" + clave + "' no puede existir.");
            return false;
        }

        Nodo nodoActual = raiz;
        notificar("Se inicia la búsqueda en la raíz " + nodoActual + ".");

        while (!nodoActual.esHoja) {
            int indiceDescenso = indiceHijo(nodoActual, clave);
            notificar("En el nodo interno " + nodoActual + " la clave '" + clave + "' corresponde al hijo de índice " + indiceDescenso + ".");
            nodoActual = nodoActual.hijos.get(indiceDescenso);
        }

        notificar("Se llegó a la hoja " + nodoActual + ". Se busca la clave '" + clave + "' dentro de ella.");
        int posicion = posicionEnHoja(nodoActual, clave);

        boolean encontrada = posicion < nodoActual.claves.size() && comparar(nodoActual.claves.get(posicion), clave) == 0;

        if (encontrada) {
            notificar("La clave está en la posición " + posicion + " de la hoja.");
        } else {
            notificar("La clave no está en la hoja. Por tanto, no se encuentra en el árbol.");
        }
        return encontrada;
    }
    
    // 3. Eliminar

    public boolean eliminar(String clave) {

        if (estaVacio()) {
            notificar("El árbol está vacío. La clave '" + clave + "' no puede eliminarse.");
            return false;
        }

        notificar("Se inicia la eliminación de '" + clave + "' en la raíz " + raiz + ".");

        boolean eliminado = eliminarRecursivo(raiz, clave);

        if (!eliminado) {
            notificar("Operación finalizada: el árbol no se modificó porque la clave '" + clave + "' no existe.");
            return false;
        }

        if (raiz.claves.isEmpty()) {
            if (raiz.esHoja) {
                notificar("La raíz es una hoja y quedó sin claves. El árbol queda vacío.");
                raiz = null;
            } else {
                notificar("La raíz quedó sin claves. Su único hijo " + raiz.hijos.get(0)
                    + " pasa a ser la nueva raíz y la altura del árbol se reduce en un nivel.");
                raiz = raiz.hijos.get(0);
            }
            notificarArbol();
        }

        if (raiz != null) {
            actualizarSeparador(raiz, clave);
        }

        notificar("Operación finalizada: la clave '" + clave + "' fue eliminada.");
        return true;
    }

    private int minimoClaves(Nodo nodo) {
        if (nodo.esHoja) {
            return minClavesHoja;
        }
        return minClavesInterno;
    }

    private boolean eliminarRecursivo(Nodo nodo, String clave) {

        if (nodo.esHoja) {
            int posicion = posicionEnHoja(nodo, clave);

            if (posicion < nodo.claves.size() && comparar(nodo.claves.get(posicion), clave) == 0) {
                notificar("Se elimina '" + clave + "' de la hoja " + nodo + ".");
                nodo.claves.remove(posicion);
                notificar("La hoja queda " + nodo + ".");
                notificarArbol();
                return true;
            }
            
            notificar("La clave '" + clave + "' no está en la hoja " + nodo + ".");
            return false;
        }

        int indiceDescenso = indiceHijo(nodo, clave);
        notificar("En el nodo interno " + nodo + " la clave '" + clave + "' corresponde al hijo de índice " + indiceDescenso + ".");
        Nodo hijo = nodo.hijos.get(indiceDescenso);
        boolean eliminado = eliminarRecursivo(hijo, clave);

        if (!eliminado) {
            return false;
        }

        if (hijo.claves.size() < minimoClaves(hijo)) {
            notificar("El nodo " + hijo + " tiene " + hijo.claves.size()
            + " claves y el mínimo es " + minimoClaves(hijo) + ". Hay subdesbordamiento.");
            corregirUnderflow(nodo, indiceDescenso);
        }
        return true;
    }

    private void corregirUnderflow(Nodo padre, int indiceHijo) {

        Nodo hijo = padre.hijos.get(indiceHijo);

        Nodo hermanoIzquierdo = null;
        if (indiceHijo > 0) {
            hermanoIzquierdo = padre.hijos.get(indiceHijo - 1);
        }
        
        Nodo hermanoDerecho = null;
        if (indiceHijo < padre.hijos.size() - 1) {
            hermanoDerecho = padre.hijos.get(indiceHijo + 1);
        }

        int minClaves = minimoClaves(hijo);
        boolean sobreHoja = hijo.esHoja;

        String tipoNodo = "el nodo interno";
        if (sobreHoja) {
            tipoNodo = "la hoja";
        }

        notificar("Se corrige el subdesbordamiento de " + tipoNodo + " "
            + hijo + ", hijo de índice " + indiceHijo + " del nodo " + padre + ".");


        if (hermanoIzquierdo != null && hermanoIzquierdo.claves.size() > minClaves) {

            notificar("El hermano izquierdo " + hermanoIzquierdo + " tiene "
                + hermanoIzquierdo.claves.size() + " claves y el mínimo es " + minClaves
                + ". Puede prestar una clave.");

            prestarDeIzquierda(padre, indiceHijo);

            if (sobreHoja) {
                notificar("La última clave del hermano izquierdo pasa al inicio de la hoja. "
                        + "El separador del padre se reemplaza por la nueva primera clave de la hoja.");
            } else {
                notificar("El separador del padre baja al inicio del nodo y la última clave "
                        + "del hermano izquierdo sube al padre.");
            }

            notificar("Resultado: hermano izquierdo " + hermanoIzquierdo + ", nodo " + hijo + ", padre " + padre + ".");
            notificarArbol();

        } else if (hermanoDerecho != null && hermanoDerecho.claves.size() > minClaves) {
            
            if (hermanoIzquierdo == null) {
                notificar("No existe hermano izquierdo.");
            } else {
                notificar("El hermano izquierdo " + hermanoIzquierdo + " tiene "
                        + hermanoIzquierdo.claves.size() + " claves y no puede prestar.");
            }

            notificar("El hermano derecho " + hermanoDerecho + " tiene "
                    + hermanoDerecho.claves.size() + " claves y el mínimo es " + minClaves
                    + ". Puede prestar una clave.");

            prestarDeDerecha(padre, indiceHijo);

            if (sobreHoja) {
                notificar("La primera clave del hermano derecho pasa al final de la hoja. "
                        + "El separador del padre se reemplaza por la nueva primera clave del hermano derecho.");
            } else {
                notificar("El separador del padre baja al final del nodo y la primera clave "
                        + "del hermano derecho sube al padre.");
            }
            notificar("Resultado: nodo " + hijo + ", hermano derecho " + hermanoDerecho + ", padre " + padre + ".");
            notificarArbol();

        } else if (hermanoIzquierdo != null) {

            String separador = padre.claves.get(indiceHijo - 1);

            notificar("El hermano izquierdo " + hermanoIzquierdo + " tiene " + hermanoIzquierdo.claves.size() + " claves y no puede prestar.");

            if (hermanoDerecho != null) {
                notificar("El hermano derecho " + hermanoDerecho + " tiene "
                        + hermanoDerecho.claves.size() + " claves y tampoco puede prestar.");
            }

            notificar("Se fusiona con el hermano izquierdo.");

            fusionarConHermano(padre, indiceHijo - 1);
            Nodo nodoFusionado = hermanoIzquierdo;

            if (sobreHoja) {
                notificar("Las claves de la hoja se agregan al hermano izquierdo y el separador '"
                        + separador + "' se elimina del padre.");
            } else {
                notificar("El separador '" + separador + "' baja del padre y se une, junto con las claves "
                        + "y los hijos del nodo, al hermano izquierdo.");
            }
            notificar("Resultado: nodo fusionado " + nodoFusionado + ", padre " + padre + ".");
            notificarArbol();

        } else if (hermanoDerecho != null) {

            String separador = padre.claves.get(indiceHijo);

            notificar("No existe hermano izquierdo y el hermano derecho " + hermanoDerecho
                    + " no puede prestar. Se fusiona con el hermano derecho.");

            fusionarConHermano(padre, indiceHijo);

            Nodo nodoFusionado = hijo;

            if (sobreHoja) {
                notificar("Las claves del hermano derecho se agregan a la hoja y el separador '"
                        + separador + "' se elimina del padre.");
            } else {
                notificar("El separador '" + separador + "' baja del padre y se une, junto con las claves "
                        + "y los hijos del hermano derecho, al nodo.");
            }
            notificar("Resultado: nodo fusionado " + nodoFusionado + ", padre " + padre + ".");
            notificarArbol();
        }
    }

    private void prestarDeIzquierda(Nodo padre, int indiceHijo) {

        Nodo hijo = padre.hijos.get(indiceHijo);
        Nodo hermanoIzquierdo = padre.hijos.get(indiceHijo - 1);

        if (hijo.esHoja) {
            String clavePrestada = hermanoIzquierdo.claves.remove(hermanoIzquierdo.claves.size() - 1);
            hijo.claves.add(0, clavePrestada);
            padre.claves.set(indiceHijo - 1, hijo.claves.get(0));
        } else {
            String bajaDelPadre = padre.claves.get(indiceHijo - 1);
            hijo.claves.add(0, bajaDelPadre);
            hijo.hijos.add(0, hermanoIzquierdo.hijos.remove(hermanoIzquierdo.hijos.size() - 1));

            String subeDelHermano = hermanoIzquierdo.claves.remove(hermanoIzquierdo.claves.size() - 1);
            padre.claves.set(indiceHijo - 1, subeDelHermano);
        }
    }

    private void prestarDeDerecha(Nodo padre, int indiceHijo) {

        Nodo hijo = padre.hijos.get(indiceHijo);
        Nodo hermanoDerecho = padre.hijos.get(indiceHijo + 1);

        if (hijo.esHoja) {
            String clavePrestada = hermanoDerecho.claves.remove(0);
            hijo.claves.add(clavePrestada);
            padre.claves.set(indiceHijo, hermanoDerecho.claves.get(0));
        } else {
            String bajaDelPadre = padre.claves.get(indiceHijo);
            hijo.claves.add(bajaDelPadre);
            hijo.hijos.add(hermanoDerecho.hijos.remove(0));

            String subeDelHermano = hermanoDerecho.claves.remove(0);
            padre.claves.set(indiceHijo, subeDelHermano);
        }
    }

    /*
    Fusiona el hijo en la posición indiceIzquierdo con el hijo en la posición indiceIzquierdo + 1.
    El resultado queda en el hijo izquierdo y se elimina el hijo derecho junto con su separador.
    */
    private void fusionarConHermano(Nodo padre, int indiceIzquierdo) {

        Nodo izquierdo = padre.hijos.get(indiceIzquierdo);
        Nodo derecho = padre.hijos.get(indiceIzquierdo + 1);
        String separador = padre.claves.get(indiceIzquierdo);

        if (izquierdo.esHoja) {
            izquierdo.claves.addAll(derecho.claves);
            izquierdo.hojaSiguiente = derecho.hojaSiguiente;
            if (derecho.hojaSiguiente != null) {
                derecho.hojaSiguiente.hojaAnterior = izquierdo;
            }
        } else {
            izquierdo.claves.add(separador);
            izquierdo.claves.addAll(derecho.claves);
            izquierdo.hijos.addAll(derecho.hijos);
        }

        padre.claves.remove(indiceIzquierdo);
        padre.hijos.remove(indiceIzquierdo + 1);
    }

    private void actualizarSeparador(Nodo nodo, String clave) {

        notificar("Se verifica si la clave '" + clave + "' permanece como separador en algún nodo interno.");
        
        Nodo actual = nodo;
        boolean reemplazado = false;

        while (!actual.esHoja) {
            int indiceDescenso = indiceHijo(actual, clave);

            if (indiceDescenso > 0 && comparar(actual.claves.get(indiceDescenso - 1), clave) == 0) {
                String separadorAnterior = actual.claves.get(indiceDescenso - 1);
                String separadorNuevo = claveMinima(actual.hijos.get(indiceDescenso));

                notificar("El nodo interno " + actual + " contiene el separador '" + separadorAnterior
                        + "'. Se reemplaza por '" + separadorNuevo
                        + "', la clave mínima de su subárbol derecho.");

                actual.claves.set(indiceDescenso - 1, separadorNuevo);
                reemplazado = true;
                notificarArbol();
            }
            actual = actual.hijos.get(indiceDescenso);
        }

        if (!reemplazado) {
            notificar("Ningún nodo interno contiene '" + clave + "' como separador. No se modifica ninguno.");
        }
    }

    private String claveMinima(Nodo nodo) {

        Nodo actual = nodo;

        while (!actual.esHoja) {
            actual = actual.hijos.get(0);
        }
        return actual.claves.get(0);
    }

    // 4. Visualizar

    public void imprimir() {
        impresor.imprimir(raiz, orden);
    }

    // 5. Mostrar proceso de busqueda, insercion y eliminacion

    public void establecerObservador(ObservadorPasos observador) {
        this.observador = observador;
    }

    private void notificar(String descripcion) {
        if (observador != null) {
            observador.paso(descripcion);
        }
    }

    private void notificarArbol() {
        if (observador != null) {
            observador.arbol(impresorConsola.generar(raiz));
        }
    }

    public String comoTexto() {
        return impresorConsola.generar(raiz);
    }

    // 6. Verificar enlaces entre hojas

    public String clavesEnOrden() {

        if (raiz == null) {
            return "(árbol vacío)";
        }

        Nodo hoja = raiz;
        while (!hoja.esHoja) {
            hoja = hoja.hijos.get(0);
        }

        StringBuilder texto = new StringBuilder();
        while (hoja != null) {
            texto.append(hoja);
            if (hoja.hojaSiguiente != null) {
                texto.append(" -> ");
            }
            hoja = hoja.hojaSiguiente;
        }
        return texto.toString();
    }

}