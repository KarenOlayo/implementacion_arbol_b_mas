package arbol_b_mas;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ImpresorImagen {

    // Medidas
    private static final int ALTO_NODO = 44;
    private static final int SEP_V = 90;       // separacion vertical entre niveles
    private static final int GAP = 40;         // separacion horizontal entre nodos
    private static final int MARGEN = 40;
    private static final int ALTO_LEYENDA = 50;

    // Colores
    private static final Color C_RAIZ = new Color(0xE67E22);      // naranja
    private static final Color C_INTERNO = new Color(0x2E86DE);   // azul
    private static final Color C_HOJA = new Color(0x27AE60);      // verde
    private static final Color C_LINEA = new Color(0x34495E);     // lineas padre-hijo
    private static final Color C_ENLACE = new Color(0x8E44AD);    // enlaces entre hojas

    private static final Font FUENTE = new Font("SansSerif", Font.BOLD, 16);

    private final Map<Nodo, Integer> anchos = new HashMap<>();
    private final Map<Nodo, int[]> pos = new HashMap<>();   // {x, y, ancho}
    private final List<Nodo> hojas = new ArrayList<>();
    private int celda;
    private Nodo raiz;

    private static JFrame ventana;
    private static JLabel etiqueta;

    public void imprimir(Nodo raiz, int orden) {
        if (raiz == null) {
            System.out.println("El árbol está vacío.");
            return;
        }
        BufferedImage img = generar(raiz);

        try {
            File carpetaImagenes = new File("imagenes");
            if (!carpetaImagenes.exists()) {
                carpetaImagenes.mkdirs();
            }

            File archivo = new File(carpetaImagenes, "arbol_bmas.png");

            ImageIO.write(img, "png", archivo);

            System.out.println(
                "Imagen del árbol generada: " + archivo.getAbsolutePath()
            );

        } catch (Exception e) {
            System.out.println(
                "No se pudo guardar la imagen: " + e.getMessage()
            );
        }

        if (GraphicsEnvironment.isHeadless()) return;
        mostrar(img, orden);
    }

    // Generacion
    private BufferedImage generar(Nodo raiz) {
        this.raiz = raiz;
        anchos.clear();
        pos.clear();
        hojas.clear();

        // Ancho de celda segun el nombre mas largo
        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D gm = tmp.createGraphics();
        FontMetrics fm = gm.getFontMetrics(FUENTE);
        celda = Math.max(60, maxTexto(raiz, fm) + 30);
        gm.dispose();

        calcularAncho(raiz);
        int prof = profundidad(raiz);
        int anchoImg = Math.max(anchos.get(raiz) + 2 * MARGEN, 460);
        int altoImg = 2 * MARGEN + prof * ALTO_NODO + (prof - 1) * SEP_V + ALTO_LEYENDA;

        colocar(raiz, 0, (anchoImg - anchos.get(raiz)) / 2);

        BufferedImage img = new BufferedImage(anchoImg, altoImg, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, anchoImg, altoImg);

        dibujarLineas(g);
        dibujarEnlacesHojas(g);
        for (Nodo n : pos.keySet()) dibujarNodo(g, n);
        dibujarLeyenda(g, anchoImg, altoImg);

        g.dispose();
        return img;
    }

    private int maxTexto(Nodo n, FontMetrics fm) {
        int m = 0;
        for (String c : n.claves) m = Math.max(m, fm.stringWidth(c));
        if (!n.esHoja) for (Nodo h : n.hijos) m = Math.max(m, maxTexto(h, fm));
        return m;
    }

    private int profundidad(Nodo n) {
        return n.esHoja ? 1 : 1 + profundidad(n.hijos.get(0));
    }

    private int anchoNodo(Nodo n) {
        return Math.max(1, n.claves.size()) * celda;
    }

    private int calcularAncho(Nodo n) {
        int propio = anchoNodo(n);
        if (n.esHoja) {
            anchos.put(n, propio);
            return propio;
        }
        int suma = GAP * (n.hijos.size() - 1);
        for (Nodo h : n.hijos) suma += calcularAncho(h);
        int w = Math.max(propio, suma);
        anchos.put(n, w);
        return w;
    }

    private void colocar(Nodo n, int nivel, int x0) {
        int w = anchos.get(n);
        int propio = anchoNodo(n);
        int cx = x0 + w / 2;
        int y = MARGEN + nivel * (ALTO_NODO + SEP_V);
        pos.put(n, new int[]{cx - propio / 2, y, propio});

        if (n.esHoja) {
            hojas.add(n);
            return;
        }
        int total = GAP * (n.hijos.size() - 1);
        for (Nodo h : n.hijos) total += anchos.get(h);
        int cursor = x0 + (w - total) / 2;
        for (Nodo h : n.hijos) {
            colocar(h, nivel + 1, cursor);
            cursor += anchos.get(h) + GAP;
        }
    }

    // Dibujo
    private Color colorDe(Nodo n) {
        if (n == raiz) return C_RAIZ;
        return n.esHoja ? C_HOJA : C_INTERNO;
    }

    private void dibujarNodo(Graphics2D g, Nodo n) {
        int[] p = pos.get(n);
        int x = p[0], y = p[1], w = p[2];
        Color base = colorDe(n);

        // Sombra
        g.setColor(new Color(0, 0, 0, 45));
        g.fill(new RoundRectangle2D.Float(x + 3, y + 4, w, ALTO_NODO, 16, 16));

        // Cuerpo con degradado
        g.setPaint(new GradientPaint(0, y, base.brighter(), 0, y + ALTO_NODO, base.darker()));
        RoundRectangle2D cuerpo = new RoundRectangle2D.Float(x, y, w, ALTO_NODO, 16, 16);
        g.fill(cuerpo);

        // Separadores entre claves
        g.setColor(new Color(255, 255, 255, 140));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i < n.claves.size(); i++) {
            g.drawLine(x + i * celda, y + 6, x + i * celda, y + ALTO_NODO - 6);
        }

        // Borde
        g.setColor(base.darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(cuerpo);

        // Texto centrado en cada celda
        g.setFont(FUENTE);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        for (int i = 0; i < n.claves.size(); i++) {
            String t = n.claves.get(i);
            int tx = x + i * celda + (celda - fm.stringWidth(t)) / 2;
            int ty = y + (ALTO_NODO + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(t, tx, ty);
        }
    }

    // Curvas continuas desde cada puntero del padre hasta el centro superior del hijo
    private void dibujarLineas(Graphics2D g) {
        g.setColor(C_LINEA);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Nodo n : pos.keySet()) {
            if (n.esHoja) continue;
            int[] p = pos.get(n);
            for (int i = 0; i < n.hijos.size(); i++) {
                int[] h = pos.get(n.hijos.get(i));
                double sx = p[0] + i * (double) p[2] / n.hijos.size();
                // los punteros nacen en los bordes entre claves
                sx = p[0] + i * (double) celda;
                double sy = p[1] + ALTO_NODO;
                double ex = h[0] + h[2] / 2.0;
                double ey = h[1];
                g.draw(new java.awt.geom.Line2D.Double(sx, sy, ex, ey));
                g.fill(new Ellipse2D.Double(sx - 4, sy - 4, 8, 8));   // punto de origen del puntero
            }
        }
    }

    // Flechas dobles entre hojas consecutivas
    private void dibujarEnlacesHojas(Graphics2D g) {
        g.setColor(C_ENLACE);
        g.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < hojas.size() - 1; i++) {
            int[] a = pos.get(hojas.get(i));
            int[] b = pos.get(hojas.get(i + 1));
            int y = a[1] + ALTO_NODO / 2;
            int x1 = a[0] + a[2] + 2;
            int x2 = b[0] - 2;
            g.drawLine(x1 + 8, y, x2 - 8, y);
            punta(g, x2, y, 1);    // hacia la derecha
            punta(g, x1, y, -1);   // hacia la izquierda
        }
    }

    private void punta(Graphics2D g, int xPunta, int y, int dir) {
        Polygon p = new Polygon();
        p.addPoint(xPunta, y);
        p.addPoint(xPunta - dir * 10, y - 6);
        p.addPoint(xPunta - dir * 10, y + 6);
        g.fillPolygon(p);
    }

    private void dibujarLeyenda(Graphics2D g, int ancho, int alto) {
        String[] textos = {"Raíz", "Nodo interno", "Hoja", "Enlace entre hojas"};
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        FontMetrics fm = g.getFontMetrics();
        int total = 0;
        for (String t : textos) total += 26 + fm.stringWidth(t) + 24;
        int x = (ancho - total) / 2;
        int y = alto - 32;
        Color[] colores = {C_RAIZ, C_INTERNO, C_HOJA, C_ENLACE};
        for (int i = 0; i < textos.length; i++) {
            g.setColor(colores[i]);
            g.fill(new RoundRectangle2D.Float(x, y, 18, 18, 6, 6));
            g.setColor(Color.DARK_GRAY);
            g.drawString(textos[i], x + 26, y + 14);
            x += 26 + fm.stringWidth(textos[i]) + 24;
        }
    }

    // Ventana
    private void mostrar(BufferedImage img, int orden) {
        SwingUtilities.invokeLater(() -> {
            if (ventana == null) {
                ventana = new JFrame();
                ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                etiqueta = new JLabel();
                ventana.add(new JScrollPane(etiqueta));
                ventana.setSize(1100, 650);
                ventana.setLocationByPlatform(true);
            }
            ventana.setTitle("Árbol B+ de orden " + orden);
            etiqueta.setIcon(new ImageIcon(img));
            etiqueta.revalidate();
            etiqueta.repaint();
            ventana.setVisible(true);
            ventana.toFront();
        });
    }
}