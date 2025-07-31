import views.*;
import javax.swing.*;
import java.awt.*;

/**
 * Clase principal de la aplicación
 * Versión mejorada con diálogo estilo Minecraft
 */
public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar look and feel por defecto
        }
        
        SwingUtilities.invokeLater(() -> {
            // Mostrar pantalla de inicio
            PantallaInicio splash = new PantallaInicio();
            splash.setVisible(true);
            
            // Timer para cambiar a la pantalla del equipo después de 5 segundos
            Timer timerSplash = new Timer(5000, e -> {
                splash.dispose();
                
                // Mostrar pantalla del equipo
                PantallaEquipo pantallaEquipo = new PantallaEquipo();
                pantallaEquipo.setVisible(true);
            });
            timerSplash.setRepeats(false);
            timerSplash.start();
        });
    }
    
    /**
     * Método ESTÁTICO llamado desde PantallaEquipo para crear la ventana principal
     */
    public static void iniciarJuego() {
        int[] dimensiones = solicitarDimensiones();
        if (dimensiones != null) {
            VentanaGameplay ventanaPrincipal = new VentanaGameplay(dimensiones[0], dimensiones[1]);
            ventanaPrincipal.setVisible(true);
        } else {
            // Si el usuario cancela, usar dimensiones por defecto
            VentanaGameplay ventanaPrincipal = new VentanaGameplay(15, 20);
            ventanaPrincipal.setVisible(true);
        }
    }
    
    /**
     * Solicita al usuario las dimensiones del laberinto con estilo Minecraft
     */
    private static int[] solicitarDimensiones() {
        // Crear panel personalizado estilo Minecraft
        JDialog dialog = new JDialog((Frame)null, "Configurar Laberinto", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        
        // Panel principal con borde
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo marrón
                g2.setColor(new Color(198, 134, 66));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Borde 3D
                // Borde claro
                g2.setColor(new Color(215, 147, 73));
                g2.fillRect(0, 0, getWidth(), 4);
                g2.fillRect(0, 0, 4, getHeight());
                
                // Borde oscuro
                g2.setColor(new Color(87, 59, 29));
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.fillRect(getWidth() - 4, 0, 4, getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Título
        JLabel titulo = new JLabel("Configuración del Tamaño del Laberinto");
        titulo.setFont(new Font("Consolas", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titulo);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Campos de entrada estilizados
        JTextField filasField = crearCampoMinecraft("15");
        JTextField columnasField = crearCampoMinecraft("20");
        
        // Panel para filas
        JPanel filasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filasPanel.setOpaque(false);
        JLabel lblFilas = new JLabel("Número de filas (5-50):");
        lblFilas.setForeground(Color.WHITE);
        lblFilas.setFont(new Font("Arial", Font.BOLD, 14));
        filasPanel.add(lblFilas);
        filasPanel.add(filasField);
        
        // Panel para columnas
        JPanel columnasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        columnasPanel.setOpaque(false);
        JLabel lblColumnas = new JLabel("Número de columnas (5-50):");
        lblColumnas.setForeground(Color.WHITE);
        lblColumnas.setFont(new Font("Arial", Font.BOLD, 14));
        columnasPanel.add(lblColumnas);
        columnasPanel.add(columnasField);
        
        contentPanel.add(filasPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(columnasPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Nota
        JLabel nota = new JLabel("Nota: Valores mayores pueden afectar el rendimiento");
        nota.setFont(new Font("Arial", Font.ITALIC, 12));
        nota.setForeground(new Color(255, 255, 200));
        nota.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nota);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Botones estilo Minecraft
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        final int[] resultado = {-1};
        
        JButton btnOk = crearBotonMinecraft("OK", new Color(0, 200, 0));
        btnOk.addActionListener(e -> {
            resultado[0] = 1;
            dialog.dispose();
        });
        
        JButton btnCancel = crearBotonMinecraft("Cancelar", new Color(200, 0, 0));
        btnCancel.addActionListener(e -> {
            resultado[0] = 0;
            dialog.dispose();
        });
        
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        
        contentPanel.add(buttonPanel);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        
        // Configurar diálogo
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        
        // Procesar resultado
        if (resultado[0] == 1) {
            try {
                int filas = Integer.parseInt(filasField.getText().trim());
                int columnas = Integer.parseInt(columnasField.getText().trim());
                
                // Validar dimensiones
                if (filas < 5 || columnas < 5) {
                    JOptionPane.showMessageDialog(null, 
                        "Las dimensiones deben ser de al menos 5x5", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return solicitarDimensiones();
                }
                
                if (filas > 50 || columnas > 50) {
                    int confirmar = JOptionPane.showConfirmDialog(null,
                        "Laberintos mayores a 50x50 pueden ser lentos.\n¿Desea continuar?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (confirmar != JOptionPane.YES_OPTION) {
                        return solicitarDimensiones();
                    }
                }
                
                return new int[]{filas, columnas};
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, 
                    "Por favor ingrese números válidos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return solicitarDimensiones();
            }
        }
        
        return null; // Usuario canceló
    }
    
    private static JTextField crearCampoMinecraft(String valorInicial) {
        JTextField field = new JTextField(valorInicial, 5);
        field.setFont(new Font("Consolas", Font.BOLD, 16));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(new Color(139, 90, 43));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(87, 59, 29), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private static JButton crearBotonMinecraft(String texto, Color color) {
        JButton button = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                    g2.fillRect(2, 2, getWidth() - 2, getHeight() - 2);
                } else {
                    // Sombra
                    g2.setColor(Color.BLACK);
                    g2.fillRect(3, 3, getWidth() - 3, getHeight() - 3);
                    
                    // Fondo
                    g2.setColor(color);
                    g2.fillRect(0, 0, getWidth() - 3, getHeight() - 3);
                    
                    // Brillo
                    g2.setColor(color.brighter());
                    g2.drawLine(0, 0, getWidth() - 4, 0);
                    g2.drawLine(0, 0, 0, getHeight() - 4);
                }
                
                // Texto
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        
        button.setFont(new Font("Consolas", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
}