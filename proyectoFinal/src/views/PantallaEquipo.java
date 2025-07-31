package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Pantalla que muestra el equipo usando la imagen de integrantes
 */
public class PantallaEquipo extends JWindow {
    private Image imagenIntegrantes;
    private Timer timerFlecha;
    private int flechaY = 0;
    private boolean subiendo = true;
    
    public PantallaEquipo() {
        // Cargar la imagen de integrantes
        cargarImagenIntegrantes();
        
        JPanel contenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Si hay imagen, mostrarla
                if (imagenIntegrantes != null) {
                    // Dibujar la imagen centrada y escalada
                    int imgWidth = imagenIntegrantes.getWidth(this);
                    int imgHeight = imagenIntegrantes.getHeight(this);
                    
                    // Calcular escala para ajustar al tamaño de la ventana
                    double scaleX = (double) getWidth() / imgWidth;
                    double scaleY = (double) getHeight() / imgHeight;
                    double scale = Math.min(scaleX, scaleY) * 0.9; // 0.9 para dejar un margen
                    
                    int scaledWidth = (int) (imgWidth * scale);
                    int scaledHeight = (int) (imgHeight * scale);
                    int x = (getWidth() - scaledWidth) / 2;
                    int y = (getHeight() - scaledHeight) / 2;
                    
                    // Fondo oscuro
                    g2.setColor(new Color(0, 0, 0));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Dibujar imagen
                    g2.drawImage(imagenIntegrantes, x, y, scaledWidth, scaledHeight, this);
                    
                    // Overlay inferior para el texto
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRect(0, getHeight() - 100, getWidth(), 100);
                    
                } else {
                    // Si no hay imagen, usar el diseño anterior como fallback
                    dibujarDisenioOriginal(g2);
                }
                
                // Flecha animada en la esquina inferior derecha
                dibujarFlechaMinecraft(g2, getWidth() - 120, getHeight() - 80 + flechaY);
                
                // Texto "Click para continuar"
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Consolas", Font.BOLD, 20));
                String clickText = "Click para continuar";
                FontMetrics fm = g2.getFontMetrics();
                int clickX = (getWidth() - fm.stringWidth(clickText)) / 2;
                int clickY = getHeight() - 30;
                g2.drawString(clickText, clickX + 2, clickY + 2);
                g2.setColor(Color.YELLOW);
                g2.drawString(clickText, clickX, clickY);
            }
            
            private void dibujarDisenioOriginal(Graphics2D g2) {
                // Fondo estilo Minecraft (degradado)
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(101, 67, 33),
                    0, getHeight(), new Color(139, 90, 43)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Marco estilo Minecraft
                dibujarMarcoMinecraft(g2);
                
                // Panel central
                int panelWidth = 600;
                int panelHeight = 400;
                int panelX = (getWidth() - panelWidth) / 2;
                int panelY = (getHeight() - panelHeight) / 2;
                
                // Fondo del panel central
                g2.setColor(new Color(198, 134, 66));
                g2.fillRect(panelX, panelY, panelWidth, panelHeight);
                
                // Borde 3D del panel
                g2.setColor(new Color(215, 147, 73));
                g2.fillRect(panelX, panelY, panelWidth, 4);
                g2.fillRect(panelX, panelY, 4, panelHeight);
                
                g2.setColor(new Color(87, 59, 29));
                g2.fillRect(panelX, panelY + panelHeight - 4, panelWidth, 4);
                g2.fillRect(panelX + panelWidth - 4, panelY, 4, panelHeight);
                
                // Logo de Minecraft
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Minecraft", Font.BOLD, 48));
                String minecraft = "MINECRAFT";
                FontMetrics fm = g2.getFontMetrics();
                int logoX = (getWidth() - fm.stringWidth(minecraft)) / 2;
                g2.drawString(minecraft, logoX + 3, panelY - 20 + 3);
                g2.setColor(new Color(85, 255, 85));
                g2.drawString(minecraft, logoX, panelY - 20);
                
                // Título
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Consolas", Font.BOLD, 36));
                String titulo = "EQUIPO DE DESARROLLO";
                fm = g2.getFontMetrics();
                int tituloX = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2.drawString(titulo, tituloX, panelY + 80);
                
                // Contenedor para los integrantes
                int integrantesY = panelY + 120;
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(panelX + 50, integrantesY, panelWidth - 100, 200, 10, 10);
                
                // Nombres del equipo
                g2.setFont(new Font("Consolas", Font.PLAIN, 24));
                String[] integrantes = {
                    "Daniel Sánchez",
                    "Joey Díaz",
                    "Daniel Durán",
                    "Nelson Villalta"
                };
                
                int yPos = integrantesY + 40;
                for (String nombre : integrantes) {
                    g2.setColor(Color.BLACK);
                    int nombreX = (getWidth() - g2.getFontMetrics().stringWidth(nombre)) / 2;
                    g2.drawString(nombre, nombreX + 2, yPos + 2);
                    
                    g2.setColor(Color.WHITE);
                    g2.drawString(nombre, nombreX, yPos);
                    yPos += 40;
                }
            }
            
            private void dibujarMarcoMinecraft(Graphics2D g2) {
                int thickness = 20;
                
                for (int i = 0; i < getWidth(); i += 40) {
                    dibujarBloque(g2, i, 0, 40, thickness);
                    dibujarBloque(g2, i, getHeight() - thickness, 40, thickness);
                }
                
                for (int i = thickness; i < getHeight() - thickness; i += 40) {
                    dibujarBloque(g2, 0, i, thickness, 40);
                    dibujarBloque(g2, getWidth() - thickness, i, thickness, 40);
                }
            }
            
            private void dibujarBloque(Graphics2D g2, int x, int y, int width, int height) {
                g2.setColor(new Color(139, 90, 43));
                g2.fillRect(x, y, width, height);
                
                g2.setColor(new Color(160, 104, 50));
                g2.drawLine(x, y, x + width - 1, y);
                g2.drawLine(x, y, x, y + height - 1);
                
                g2.setColor(new Color(87, 59, 29));
                g2.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 1);
                g2.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
            }
            
            private void dibujarFlechaMinecraft(Graphics2D g2, int x, int y) {
                int[][] flecha = {
                    {0,0,0,1,1,0,0,0},
                    {0,0,1,1,1,1,0,0},
                    {0,1,1,1,1,1,1,0},
                    {1,1,1,1,1,1,1,1},
                    {0,0,1,1,1,1,0,0},
                    {0,0,1,1,1,1,0,0},
                    {0,0,1,1,1,1,0,0},
                    {0,0,1,1,1,1,0,0}
                };
                
                int pixelSize = 5;
                for (int i = 0; i < flecha.length; i++) {
                    for (int j = 0; j < flecha[i].length; j++) {
                        if (flecha[i][j] == 1) {
                            g2.setColor(Color.WHITE);
                            g2.fillRect(x + j * pixelSize, y + i * pixelSize, pixelSize, pixelSize);
                            g2.setColor(Color.LIGHT_GRAY);
                            g2.drawRect(x + j * pixelSize, y + i * pixelSize, pixelSize, pixelSize);
                        }
                    }
                }
            }
        };
        
        contenido.setLayout(new BorderLayout());
        
        // Agregar listener para click
        contenido.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                avanzar();
            }
        });
        
        // Animación de la flecha
        timerFlecha = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (subiendo) {
                    flechaY -= 2;
                    if (flechaY <= -10) {
                        subiendo = false;
                    }
                } else {
                    flechaY += 2;
                    if (flechaY >= 0) {
                        subiendo = true;
                    }
                }
                repaint();
            }
        });
        timerFlecha.start();
        
        setContentPane(contenido);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void cargarImagenIntegrantes() {
        // Intentar múltiples ubicaciones para la imagen
        String[] rutasPosibles = {
            "src/images/integrantes.jpg",
            "src/images/integrantes.png",
            "images/integrantes.jpg",
            "images/integrantes.png",
            "src/images/equipo.jpg",
            "src/images/equipo.png",
            "src/images/team.jpg",
            "src/images/team.png"
        };
        
        // Primero intentar como recurso
        String[] nombresRecurso = {
            "/images/integrantes.jpg",
            "/images/integrantes.png",
            "/images/equipo.jpg",
            "/images/equipo.png"
        };
        
        for (String nombre : nombresRecurso) {
            try {
                imagenIntegrantes = ImageIO.read(getClass().getResourceAsStream(nombre));
                if (imagenIntegrantes != null) {
                    System.out.println("Imagen de integrantes cargada desde recursos: " + nombre);
                    return;
                }
            } catch (Exception e) {
                // Continuar con otros métodos
            }
        }
        
        // Luego intentar como archivo
        for (String ruta : rutasPosibles) {
            try {
                File archivo = new File(ruta);
                if (archivo.exists()) {
                    imagenIntegrantes = ImageIO.read(archivo);
                    System.out.println("Imagen de integrantes cargada desde: " + archivo.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                // Continuar con siguiente ruta
            }
        }
        
        System.err.println("No se pudo cargar la imagen de integrantes");
        System.err.println("Por favor, coloca la imagen en 'src/images/' con uno de estos nombres:");
        System.err.println("- integrantes.jpg o integrantes.png");
        System.err.println("- equipo.jpg o equipo.png");
    }
    
    private void avanzar() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                Class<?> appClass = Class.forName("App");
                java.lang.reflect.Method method = appClass.getMethod("iniciarJuego");
                method.invoke(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                VentanaGameplay ventana = new VentanaGameplay(15, 20);
                ventana.setVisible(true);
            }
        });
    }
    
    @Override
    public void dispose() {
        if (timerFlecha != null) {
            timerFlecha.stop();
        }
        super.dispose();
    }
}