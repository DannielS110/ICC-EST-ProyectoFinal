package views;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Pantalla de inicio con imagen de fondo
 */
public class PantallaInicio extends JWindow {
    private Image imagenFondo;
    
    public PantallaInicio() {
        // Intentar cargar imagen de múltiples fuentes
        cargarImagen();
        
        JPanel contenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Dibujar imagen de fondo
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Si no hay imagen, usar fondo degradado estilo Minecraft
                    Graphics2D g2 = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(139, 195, 74),
                        0, getHeight(), new Color(46, 125, 50)
                    );
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Dibujar título Minecraft si no hay imagen
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Minecraft", Font.BOLD, 60));
                    String titulo = "MINECRAFT";
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                    int y = getHeight() / 3;
                    g2.drawString(titulo, x + 3, y + 3);
                    g2.setColor(new Color(85, 255, 85));
                    g2.drawString(titulo, x, y);
                    
                    // Subtítulo
                    g2.setFont(new Font("Consolas", Font.BOLD, 24));
                    g2.setColor(Color.WHITE);
                    String subtitulo = "Maze Solver Edition";
                    fm = g2.getFontMetrics();
                    x = (getWidth() - fm.stringWidth(subtitulo)) / 2;
                    g2.drawString(subtitulo, x, y + 50);
                }
                
                // Overlay semi-transparente para el texto
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, getHeight() - 150, getWidth(), 150);
            }
        };
        
        contenido.setLayout(new BorderLayout());
        
        // Panel inferior con barra de progreso
        JPanel panelInferior = new JPanel();
        panelInferior.setOpaque(false);
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        
        JLabel lblCargando = new JLabel("Cargando...");
        lblCargando.setFont(new Font("Consolas", Font.BOLD, 20));
        lblCargando.setForeground(Color.WHITE);
        lblCargando.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        progressBar.setMaximumSize(new Dimension(300, 20));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelInferior.add(Box.createVerticalStrut(50));
        panelInferior.add(lblCargando);
        panelInferior.add(Box.createVerticalStrut(10));
        panelInferior.add(progressBar);
        panelInferior.add(Box.createVerticalStrut(30));
        
        contenido.add(panelInferior, BorderLayout.SOUTH);
        
        setContentPane(contenido);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void cargarImagen() {
        // Intentar múltiples ubicaciones para la imagen
        String[] rutasPosibles = {
            "src/images/inicio.jpg",
            "images/inicio.jpg",
            "../images/inicio.jpg",
            "resources/images/inicio.jpg"
        };
        
        // Primero intentar como recurso
        try {
            imagenFondo = ImageIO.read(getClass().getResourceAsStream("/images/inicio.jpg"));
            if (imagenFondo != null) {
                System.out.println("Imagen cargada desde recursos");
                return;
            }
        } catch (Exception e) {
            // Continuar con otros métodos
        }
        
        // Luego intentar como archivo
        for (String ruta : rutasPosibles) {
            try {
                File archivo = new File(ruta);
                if (archivo.exists()) {
                    imagenFondo = ImageIO.read(archivo);
                    System.out.println("Imagen cargada desde: " + archivo.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                // Continuar con siguiente ruta
            }
        }
        
        System.err.println("No se pudo cargar la imagen de inicio desde ninguna ubicación");
        System.err.println("Asegúrate de colocar 'inicio.jpg' en la carpeta 'src/images/'");
    }
}