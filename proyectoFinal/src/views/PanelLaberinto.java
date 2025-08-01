package views;

import model.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Panel mejorado para dibujar el laberinto con estilo Minecraft y animaciones
 * VERSIÓN FINAL CORREGIDA: Animación correcta y persistencia de colores
 */
public class PanelLaberinto extends JPanel {
    private Laberinto laberinto;
    private ResultadoEjecucion resultadoActual;
    private int tamanioCelda = 25;
    private ModoEdicion modoActual = ModoEdicion.NORMAL;
    private int velocidadAnimacion = 50;
    
    // Variables para el centrado
    private int offsetX = 0;
    private int offsetY = 0;
    
    // Estados de animación
    private List<Celda> celdasAnimadasVisitadas = new CopyOnWriteArrayList<>();
    private List<Celda> celdasAnimadasCamino = new CopyOnWriteArrayList<>();
    private Celda celdaActualAnimacion = null;
    private Timer timerAnimacion;
    private boolean animacionEnProgreso = false;
    
    // Colores estilo Minecraft mejorados
    private final Color COLOR_PARED = new Color(139, 69, 19);
    private final Color COLOR_CAMINO = new Color(144, 238, 144);
    private final Color COLOR_INICIO = new Color(0, 255, 0);
    private final Color COLOR_FIN = new Color(255, 0, 0);
    private final Color COLOR_VISITADA = new Color(173, 216, 230, 180);
    private final Color COLOR_EVALUANDO = new Color(255, 255, 0, 200);
    private final Color COLOR_SOLUCION = new Color(255, 215, 0);
    private final Color COLOR_BORDE = new Color(101, 67, 33);
    
    // Enum para los modos de edición
    public enum ModoEdicion {
        NORMAL, EDITAR_PAREDES, PONER_INICIO, PONER_FIN
    }
    
    public PanelLaberinto(Laberinto laberinto) {
        this.laberinto = laberinto;
        
        // Configurar panel
        setBackground(new Color(135, 206, 235));
        actualizarTamanio();
        
        // Agregar mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!animacionEnProgreso) {
                    manejarClick(e);
                }
            }
        });
        
        // Agregar mouse motion listener para hover effect
        addMouseMotionListener(new MouseMotionAdapter() {
            private Point ultimaPosicion = null;
            
            @Override
            public void mouseMoved(MouseEvent e) {
                Point nuevaPosicion = getCeldaEnPosicion(e.getX(), e.getY());
                if ((nuevaPosicion == null && ultimaPosicion != null) || 
                    (nuevaPosicion != null && !nuevaPosicion.equals(ultimaPosicion))) {
                    ultimaPosicion = nuevaPosicion;
                    repaint();
                }
            }
        });
    }
    
    /**
     * Calcula los offsets para centrar el laberinto
     */
    private void calcularOffsets() {
        int totalAncho = laberinto.getColumnas() * tamanioCelda;
        int totalAlto = laberinto.getFilas() * tamanioCelda;
        offsetX = (getWidth() - totalAncho) / 2;
        offsetY = (getHeight() - totalAlto) / 2;
        
        // Asegurar que los offsets no sean negativos
        offsetX = Math.max(20, offsetX);
        offsetY = Math.max(20, offsetY);
    }
    
    private Point getCeldaEnPosicion(int x, int y) {
        // Calcular offsets actuales
        calcularOffsets();
        
        // Ajustar coordenadas con el offset
        int fila = (y - offsetY) / tamanioCelda;
        int columna = (x - offsetX) / tamanioCelda;
        
        // Verificar si está dentro de los límites
        if (laberinto.esValida(fila, columna)) {
            return new Point(columna, fila);
        }
        return null;
    }
    
    private void actualizarTamanio() {
        // Tamaño mínimo para permitir scroll si es necesario
        int anchoMinimo = laberinto.getColumnas() * tamanioCelda + 40;
        int altoMinimo = laberinto.getFilas() * tamanioCelda + 40;
        setPreferredSize(new Dimension(anchoMinimo, altoMinimo));
        revalidate();
    }
    
    public void setLaberinto(Laberinto nuevoLaberinto) {
        this.laberinto = nuevoLaberinto;
        limpiarAnimacion();
        actualizarTamanio();
        repaint();
    }
    
    private void manejarClick(MouseEvent e) {
        Point celda = getCeldaEnPosicion(e.getX(), e.getY());
        if (celda == null) return;
        
        int fila = celda.y;
        int columna = celda.x;
        
        System.out.println("Click en fila=" + fila + ", columna=" + columna + ", modo=" + modoActual);
        
        switch (modoActual) {
            case EDITAR_PAREDES:
                laberinto.alternarCelda(fila, columna);
                limpiarResultado();
                repaint();
                break;
                
            case PONER_INICIO:
                laberinto.setInicio(fila, columna);
                limpiarResultado();
                repaint();
                break;
                
            case PONER_FIN:
                laberinto.setFin(fila, columna);
                limpiarResultado();
                repaint();
                break;
                
            case NORMAL:
                // No hacer nada en modo normal
                break;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar fondo
        dibujarFondo(g2);

        // Calcular offsets para centrado
        calcularOffsets();

        // Obtener posición del mouse para hover effect
        Point mousePos = getMousePosition();
        Point celdaHover = null;
        if (mousePos != null && modoActual != ModoEdicion.NORMAL) {
            celdaHover = getCeldaEnPosicion(mousePos.x, mousePos.y);
        }

        // Dibujar el laberinto centrado
        for (int i = 0; i < laberinto.getFilas(); i++) {
            for (int j = 0; j < laberinto.getColumnas(); j++) {
                int x = j * tamanioCelda + offsetX;
                int y = i * tamanioCelda + offsetY;

                Celda celda = laberinto.getCelda(i, j);
                Color color = obtenerColorCelda(celda);

                // Aplicar hover effect
                boolean esHover = celdaHover != null && celdaHover.x == j && celdaHover.y == i;

                // Dibujar celda
                dibujarCelda3D(g2, x, y, color, esHover, celda.equals(celdaActualAnimacion));

                // Dibujar símbolos
                dibujarSimbolos(g2, celda, x, y);
            }
        }

        // Dibujar info del modo
        dibujarInfoModo(g2);

        // Dibujar estado de animación
        if (animacionEnProgreso) {
            dibujarEstadoAnimacion(g2);
        }
    }
    
    private void dibujarFondo(Graphics2D g2) {
        // Patrón de cuadrícula sutil
        g2.setColor(new Color(135, 206, 235, 50));
        for (int i = 0; i < getWidth(); i += 50) {
            for (int j = 0; j < getHeight(); j += 50) {
                g2.drawRect(i, j, 50, 50);
            }
        }
    }
    
    /**
     * CORREGIDO: Obtiene el color correcto de la celda mostrando visitadas y camino
     */
    private Color obtenerColorCelda(Celda celda) {
        // Prioridad de colores
        if (celda.equals(laberinto.getInicio())) {
            return COLOR_INICIO;
        }
        if (celda.equals(laberinto.getFin())) {
            return COLOR_FIN;
        }
        
        // Durante la animación
        if (animacionEnProgreso) {
            if (celda.equals(celdaActualAnimacion)) {
                return COLOR_EVALUANDO;
            }
            if (celdasAnimadasCamino.contains(celda)) {
                return COLOR_SOLUCION;
            }
            if (celdasAnimadasVisitadas.contains(celda)) {
                return COLOR_VISITADA;
            }
        }
        
        // Después de la animación - MANTENER LOS COLORES
        if (!animacionEnProgreso && resultadoActual != null) {
            // Primero verificar si está en el camino final
            if (celda.isEnCamino()) {
                return COLOR_SOLUCION;
            }
            // Luego verificar si fue visitada
            if (celda.isVisitada()) {
                return COLOR_VISITADA;
            }
        }
        
        return celda.esPared() ? COLOR_PARED : COLOR_CAMINO;
    }
    
    private void dibujarCelda3D(Graphics2D g2, int x, int y, Color color, boolean hover, boolean destacar) {
        // Efecto de hover
        if (hover && !animacionEnProgreso) {
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRect(x - 2, y - 2, tamanioCelda + 2, tamanioCelda + 2);
        }
        
        // Efecto de destacado durante animación
        if (destacar) {
            // Pulso de luz
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRect(x - 3, y - 3, tamanioCelda + 4, tamanioCelda + 4);
        }
        
        // Sombra
        g2.setColor(color.darker().darker());
        g2.fillRect(x + 2, y + 2, tamanioCelda - 2, tamanioCelda - 2);
        
        // Celda principal
        g2.setColor(color);
        g2.fillRect(x, y, tamanioCelda - 2, tamanioCelda - 2);
        
        // Borde superior e izquierdo (luz)
        g2.setColor(color.brighter());
        g2.drawLine(x, y, x + tamanioCelda - 3, y);
        g2.drawLine(x, y, x, y + tamanioCelda - 3);
        
        // Borde inferior y derecho (sombra)
        g2.setColor(COLOR_BORDE);
        g2.drawLine(x + tamanioCelda - 2, y, x + tamanioCelda - 2, y + tamanioCelda - 2);
        g2.drawLine(x, y + tamanioCelda - 2, x + tamanioCelda - 2, y + tamanioCelda - 2);
    }
    
    private void dibujarSimbolos(Graphics2D g2, Celda celda, int x, int y) {
        g2.setFont(new Font("Consolas", Font.BOLD, 16));
        
        if (celda.equals(laberinto.getInicio())) {
            g2.setColor(Color.WHITE);
            g2.drawString("A", x + 6, y + 18);
        }
        
        if (celda.equals(laberinto.getFin())) {
            g2.setColor(Color.WHITE);
            g2.drawString("B", x + 6, y + 18);
        }
    }
    
    private void dibujarInfoModo(Graphics2D g2) {
        String texto = "";
        Color color = Color.BLACK;
        
        switch (modoActual) {
            case EDITAR_PAREDES:
                texto = "Click para alternar paredes";
                color = COLOR_PARED;
                break;
            case PONER_INICIO:
                texto = "Click para colocar inicio (A)";
                color = COLOR_INICIO;
                break;
            case PONER_FIN:
                texto = "Click para colocar fin (B)";
                color = COLOR_FIN;
                break;
            case NORMAL:
                // No mostrar nada en modo normal
                break;
        }
        
        if (!texto.isEmpty()) {
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(10, getHeight() - 35, 250, 25, 10, 10);
            
            g2.setColor(color);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(texto, 20, getHeight() - 15);
        }
    }
    
    private void dibujarEstadoAnimacion(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(getWidth() - 200, 10, 180, 30, 10, 10);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Resolviendo...", getWidth() - 190, 30);
        
        // Animación de puntos
        int puntos = (int)(System.currentTimeMillis() / 500) % 4;
        String dots = "";
        for (int i = 0; i < puntos; i++) {
            dots += ".";
        }
        g2.drawString(dots, getWidth() - 90, 30);
    }
    
    public void animarSolucion(ResultadoEjecucion resultado) {
        if (resultado == null || animacionEnProgreso) return;
        limpiarAnimacion();
        this.resultadoActual = resultado;
        animacionEnProgreso = true;

        // Obtener orden de celdas visitadas
        List<Celda> ordenVisitasTmp = resultado.getOrdenVisitas();
        final List<Celda> ordenVisitas = (ordenVisitasTmp == null) ? new ArrayList<>() : ordenVisitasTmp;

        final int[] indice = {0};
        timerAnimacion = new Timer(velocidadAnimacion, null);

        timerAnimacion.addActionListener(e -> {
            if (indice[0] < ordenVisitas.size()) {
                Celda celda = ordenVisitas.get(indice[0]);
                celdasAnimadasVisitadas.add(celda);
                celdaActualAnimacion = celda; // Nuevo: para destacar la celda actual si quieres efecto
                repaint();
                indice[0]++;
            } else {
                timerAnimacion.stop();
                celdaActualAnimacion = null;
                // Cuando termina de pintar visitadas, ahora pinta el camino de solución en amarillo
                if (resultado.isEncontroSolucion()) {
                    List<Celda> caminoSinInicioFin = new ArrayList<>();
                    for (Celda c : resultado.getCamino()) {
                        if (!c.equals(laberinto.getInicio()) && !c.equals(laberinto.getFin())) {
                            caminoSinInicioFin.add(c);
                        }
                    }
                    animarCaminoFinal(caminoSinInicioFin);
                } else {
                    animacionEnProgreso = false;
                    JOptionPane.showMessageDialog(this, 
                        "No se encontró solución", 
                        "Sin solución", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        timerAnimacion.start();
    }



    /**
     * Ahora solo animamos el camino sobre las visitadas,
     * opcionalmente puedes eliminar la celda del camino de las visitadas para
     * que solo se vea dorada
     */
    private void animarCaminoFinal(List<Celda> camino) {
        if (camino.isEmpty()) {
            finalizarAnimacion();
            return;
        }
        
        final int[] indice = {0};
        Timer timerCamino = new Timer(velocidadAnimacion * 2, null);
        
        timerCamino.addActionListener(e -> {
            if (indice[0] < camino.size()) {
                Celda celda = camino.get(indice[0]);
                celdaActualAnimacion = celda;
                celdasAnimadasCamino.add(celda);
                // Opcional: quitar de visitadas si quieres que sea SOLO dorado
                celdasAnimadasVisitadas.remove(celda);
                repaint();
                indice[0]++;
            } else {
                timerCamino.stop();
                finalizarAnimacion();
            }
        });
        
        timerCamino.start();
    }
    /**
     * NUEVO: Finaliza la animación y mantiene los colores
     */
    private void finalizarAnimacion() {
        celdaActualAnimacion = null;
        animacionEnProgreso = false;
        
        // IMPORTANTE: Mantener el estado en el modelo
        // Las celdas ya están marcadas como visitadas y en camino
        // Solo necesitamos repintar para mostrar el estado final
        
        repaint();
        
        System.out.println("Animación finalizada. Celdas visitadas: " + celdasAnimadasVisitadas.size() + 
                          ", Camino: " + celdasAnimadasCamino.size());
    }
    
    public void detenerAnimacion() {
        if (timerAnimacion != null && timerAnimacion.isRunning()) {
            timerAnimacion.stop();
        }
        animacionEnProgreso = false;
        celdaActualAnimacion = null;
        repaint();
    }
    
    private void limpiarAnimacion() {
        detenerAnimacion();
        celdasAnimadasVisitadas.clear();
        celdasAnimadasCamino.clear();
        celdaActualAnimacion = null;
    }
    
    public void setModoEdicion(ModoEdicion modo) {
        this.modoActual = modo;
        repaint();
    }
    
    /**
     * Establece la velocidad de animación
     * @param velocidad velocidad en milisegundos entre frames
     */
    public void setVelocidadAnimacion(int velocidad) {
        this.velocidadAnimacion = velocidad;
        // Si hay una animación en curso, actualizar el timer
        if (timerAnimacion != null && timerAnimacion.isRunning()) {
            timerAnimacion.setDelay(velocidad);
        }
    }
    
    /**
     * Establece el resultado sin animación
     */
    public void setResultado(ResultadoEjecucion resultado) {
        limpiarAnimacion();
        this.resultadoActual = resultado;
        
        if (resultado != null && resultado.isEncontroSolucion()) {
            // Marcar el camino
            for (Celda c : resultado.getCamino()) {
                c.setEnCamino(true);
            }
        }
        repaint();
    }
    
    /**
     * CORREGIDO: Limpia completamente el resultado y los estados
     */
    public void limpiarResultado() {
        limpiarAnimacion();
        this.resultadoActual = null;
        
        // Limpiar estados del laberinto
        for (int i = 0; i < laberinto.getFilas(); i++) {
            for (int j = 0; j < laberinto.getColumnas(); j++) {
                Celda celda = laberinto.getCelda(i, j);
                celda.setVisitada(false);
                celda.setEnCamino(false);
            }
        }
        
        repaint();
    }
    
    public boolean isAnimacionEnProgreso() {
        return animacionEnProgreso;
    }
}