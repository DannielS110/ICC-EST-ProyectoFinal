package views;

import controllers.*;
import model.*;
import data.GuardadorDatos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.util.*;

/**
 * Ventana principal del juego con estilo Minecraft mejorada
 * CORRECCIONES APLICADAS A LOS BOTONES
 */
public class VentanaGameplay extends JFrame {
    private Laberinto laberinto;
    private PanelLaberinto panelLaberinto;
    private GuardadorDatos guardador;
    
    // Componentes de la interfaz
    private JLabel lblTiempo;
    private JLabel lblCeldas;
    private JLabel lblLongitud;
    private JLabel lblEstado;
    private JComboBox<String> comboAlgoritmos;
    private JButton btnResolver;
    private JButton btnDetener;
    private JSpinner spinnerVelocidad;
    
    // Colores Minecraft
    private final Color COLOR_FONDO = new Color(139, 195, 74);
    private final Color COLOR_BOTON = new Color(144, 164, 174);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_PANEL = new Color(101, 67, 33);
    
    /**
     * Constructor con dimensiones específicas
     */
    public VentanaGameplay(int filas, int columnas) {
        super("Resolvedor de Laberintos - Minecraft Edition");
        
        // Inicializar componentes con dimensiones específicas
        this.laberinto = new Laberinto(filas, columnas);
        this.panelLaberinto = new PanelLaberinto(laberinto);
        this.guardador = new GuardadorDatos();
        
        configurarVentana();
        inicializarComponentes();
        
        // Generar un laberinto inicial
        laberinto.generarLaberintoSimple();
        panelLaberinto.repaint();
    }
    
    /**
     * Constructor por defecto
     */
    public VentanaGameplay() {
        this(15, 20); // Dimensiones por defecto
    }
    
    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        
        // Menú completo
        setJMenuBar(crearMenuCompleto());
        
        // Intentar cargar icono
        try {
            Image icono = ImageIO.read(getClass().getResourceAsStream("/images/inicio.jpg"));
            if (icono != null) {
                setIconImage(icono);
            }
        } catch (Exception e) {
            // Usar icono por defecto si falla
        }
    }
    
    private JMenuBar crearMenuCompleto() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemNuevo = new JMenuItem("Nuevo Laberinto");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itemNuevo.addActionListener(e -> nuevoLaberinto());
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        itemSalir.addActionListener(e -> System.exit(0));
        
        menuArchivo.add(itemNuevo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Edición
        JMenu menuEdicion = new JMenu("Edición");
        JMenuItem itemLimpiar = new JMenuItem("Limpiar Laberinto");
        itemLimpiar.addActionListener(e -> limpiarLaberinto());
        
        JMenuItem itemGenerar = new JMenuItem("Generar Laberinto Aleatorio");
        itemGenerar.addActionListener(e -> generarLaberinto());
        
        menuEdicion.add(itemLimpiar);
        menuEdicion.add(itemGenerar);
        
        // Menú Algoritmos
        JMenu menuAlgoritmos = new JMenu("Algoritmos");
        JMenuItem itemEjecutar = new JMenuItem("Ejecutar Seleccionado");
        itemEjecutar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        itemEjecutar.addActionListener(e -> resolver());
        
        JMenuItem itemComparar = new JMenuItem("Comparar Todos");
        itemComparar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemComparar.addActionListener(e -> compararTodos());
        
        JMenuItem itemDetener = new JMenuItem("Detener Ejecución");
        itemDetener.addActionListener(e -> panelLaberinto.detenerAnimacion());
        
        menuAlgoritmos.add(itemEjecutar);
        menuAlgoritmos.add(itemComparar);
        menuAlgoritmos.addSeparator();
        menuAlgoritmos.add(itemDetener);
        
        // Menú Ver
        JMenu menuVer = new JMenu("Ver");
        JMenuItem itemEstadisticas = new JMenuItem("Estadísticas");
        itemEstadisticas.addActionListener(e -> mostrarEstadisticas());
        
        JMenuItem itemGraficos = new JMenuItem("Gráficos");
        itemGraficos.addActionListener(e -> mostrarGraficos());
        
        menuVer.add(itemEstadisticas);
        menuVer.add(itemGraficos);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> mostrarAcercaDe());
        
        menuAyuda.add(itemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuEdicion);
        menuBar.add(menuAlgoritmos);
        menuBar.add(menuVer);
        menuBar.add(menuAyuda);
        
        return menuBar;
    }
    
    private void inicializarComponentes() {
        // Panel superior
        add(crearPanelSuperior(), BorderLayout.NORTH);
        
        // Panel central con el laberinto
        JScrollPane scrollPane = new JScrollPane(panelLaberinto);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(135, 206, 235));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel lateral
        add(crearPanelLateral(), BorderLayout.EAST);
        
        // Panel inferior
        add(crearPanelInferior(), BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 700));
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Título centrado
        JLabel titulo = new JLabel("RESOLVEDOR DE LABERINTOS");
        titulo.setFont(new Font("Consolas", Font.BOLD, 28));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.CENTER);
        
        // Información del laberinto
        JLabel infoLaberinto = new JLabel(String.format("Laberinto: %d x %d", 
            laberinto.getFilas(), laberinto.getColumnas()));
        infoLaberinto.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLaberinto.setForeground(COLOR_TEXTO);
        panel.add(infoLaberinto, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(250, 0));
        
        // Sección Laberinto
        agregarSeccion(panel, "LABERINTO");
        
        // CORRECCIÓN: Asegurar que los botones funcionen correctamente
        JButton btnGenerar = crearBoton("Generar Nuevo", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Botón Generar Nuevo presionado"); // Debug
                generarLaberinto();
            }
        });
        
        JButton btnLimpiar = crearBoton("Limpiar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Botón Limpiar presionado"); // Debug
                limpiarLaberinto();
            }
        });
        
        panel.add(btnGenerar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLimpiar);
        panel.add(Box.createVerticalStrut(20));
        
        // Sección Edición
        agregarSeccion(panel, "EDICION");
        
        JButton btnEditarParedes = crearBoton("Editar Paredes", e -> {
            System.out.println("Modo Editar Paredes activado");
            panelLaberinto.setModoEdicion(PanelLaberinto.ModoEdicion.EDITAR_PAREDES);
        });
        
        JButton btnPonerInicio = crearBoton("Poner Inicio (A)", e -> {
            System.out.println("Modo Poner Inicio activado");
            panelLaberinto.setModoEdicion(PanelLaberinto.ModoEdicion.PONER_INICIO);
        });
        
        JButton btnPonerFin = crearBoton("Poner Fin (B)", e -> {
            System.out.println("Modo Poner Fin activado");
            panelLaberinto.setModoEdicion(PanelLaberinto.ModoEdicion.PONER_FIN);
        });
        
        panel.add(btnEditarParedes);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPonerInicio);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPonerFin);
        panel.add(Box.createVerticalStrut(20));
        
        // Sección Algoritmos
        agregarSeccion(panel, "ALGORITMO");
        comboAlgoritmos = new JComboBox<>(new String[]{
            "BFS (Breadth-First Search)",
            "DFS (Depth-First Search)",
            "Recursivo 2 direcciones",
            "Recursivo 4 direcciones",
            "Recursivo 4 direcciones con backtracking"
        });
        comboAlgoritmos.setMaximumSize(new Dimension(200, 30));
        comboAlgoritmos.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(comboAlgoritmos);
        panel.add(Box.createVerticalStrut(10));
        
        // Control de velocidad
        JLabel lblVelocidad = new JLabel("Velocidad animación:");
        lblVelocidad.setFont(new Font("Arial", Font.PLAIN, 12));
        lblVelocidad.setForeground(COLOR_TEXTO);
        lblVelocidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblVelocidad);
        
        spinnerVelocidad = new JSpinner(new SpinnerNumberModel(50, 10, 200, 10));
        spinnerVelocidad.setMaximumSize(new Dimension(100, 25));
        panel.add(spinnerVelocidad);
        panel.add(Box.createVerticalStrut(20));
        
        // Botones de acción
        btnResolver = crearBoton("RESOLVER", e -> resolver());
        btnResolver.setBackground(new Color(0, 200, 0));
        panel.add(btnResolver);
        panel.add(Box.createVerticalStrut(10));
        
        btnDetener = crearBoton("DETENER", e -> panelLaberinto.detenerAnimacion());
        btnDetener.setBackground(new Color(200, 0, 0));
        btnDetener.setEnabled(false);
        panel.add(btnDetener);
        panel.add(Box.createVerticalStrut(15));
        
        JButton btnComparar = crearBoton("Comparar Todos", e -> compararTodos());
        btnComparar.setBackground(new Color(255, 140, 0));
        panel.add(btnComparar);
        panel.add(Box.createVerticalStrut(15));
        
        JButton btnEstadisticas = crearBoton("Ver Estadísticas", e -> mostrarEstadisticas());
        panel.add(btnEstadisticas);
        panel.add(Box.createVerticalStrut(10));
        
        JButton btnGraficos = crearBoton("Ver Gráficos", e -> mostrarGraficos());
        btnGraficos.setBackground(new Color(100, 150, 200));
        panel.add(btnGraficos);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Botón de salir
        JButton btnSalir = crearBoton("SALIR", e -> salirPrograma());
        btnSalir.setBackground(new Color(150, 50, 50));
        panel.add(btnSalir);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        lblTiempo = crearEtiqueta("-- ms");
        lblCeldas = crearEtiqueta("--");
        lblLongitud = crearEtiqueta("--");
        lblEstado = crearEtiqueta("Listo");
        
        panel.add(crearPanelEstadistica("TIEMPO", lblTiempo));
        panel.add(crearPanelEstadistica("CELDAS VISITADAS", lblCeldas));
        panel.add(crearPanelEstadistica("LONGITUD CAMINO", lblLongitud));
        panel.add(crearPanelEstadistica("ESTADO", lblEstado));
        
        return panel;
    }
    
    // Métodos auxiliares
    private void agregarSeccion(JPanel panel, String titulo) {
        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Consolas", Font.BOLD, 16));
        label.setForeground(COLOR_TEXTO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
    }
    
    private JButton crearBoton(String texto, ActionListener listener) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                    g2.fillRect(2, 2, getWidth() - 2, getHeight() - 2);
                } else {
                    // Sombra
                    g2.setColor(Color.BLACK);
                    g2.fillRect(3, 3, getWidth() - 3, getHeight() - 3);
                    
                    // Fondo
                    g2.setColor(getBackground());
                    g2.fillRect(0, 0, getWidth() - 3, getHeight() - 3);
                    
                    // Brillo
                    g2.setColor(getBackground().brighter());
                    g2.drawLine(0, 0, getWidth() - 4, 0);
                    g2.drawLine(0, 0, 0, getHeight() - 4);
                }
                
                // Texto
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        
        boton.setFont(new Font("Consolas", Font.BOLD, 12));
        boton.setBackground(COLOR_BOTON);
        boton.setForeground(COLOR_TEXTO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setMaximumSize(new Dimension(200, 35));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);
        
        return boton;
    }
    
    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Consolas", Font.PLAIN, 16));
        label.setForeground(COLOR_TEXTO);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    private JPanel crearPanelEstadistica(String titulo, JLabel valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 12));
        lblTitulo.setForeground(Color.DARK_GRAY);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(valor, BorderLayout.CENTER);
        
        return panel;
    }
    
    // CORRECCIÓN: Métodos de acción implementados correctamente
    private void generarLaberinto() {
        System.out.println("Generando nuevo laberinto..."); // Debug
        
        // Mostrar diálogo de configuración de tamaño
        int[] dimensiones = solicitarDimensionesNuevo();
        if (dimensiones != null) {
            // Crear nuevo laberinto con las dimensiones especificadas
            this.laberinto = new Laberinto(dimensiones[0], dimensiones[1]);
            this.laberinto.generarLaberintoSimple();
            panelLaberinto.setLaberinto(this.laberinto);
            limpiarEstadisticas();
            lblEstado.setText("Nuevo laberinto generado");
            
            // Actualizar información en el panel superior
            Component[] components = ((JPanel)getContentPane().getComponent(0)).getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel && ((JLabel)comp).getText().contains("Laberinto:")) {
                    ((JLabel)comp).setText(String.format("Laberinto: %d x %d", dimensiones[0], dimensiones[1]));
                    break;
                }
            }
            
            // Redimensionar ventana si es necesario
            pack();
        }
    }
    
    /**
     * Solicita al usuario las dimensiones para un nuevo laberinto
     * @return array con [filas, columnas] o null si cancela
     */
    private int[] solicitarDimensionesNuevo() {
        // Crear panel personalizado estilo Minecraft
        JDialog dialog = new JDialog(this, "Configurar Nuevo Laberinto", true);
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
        JLabel titulo = new JLabel("Configuración del Nuevo Laberinto");
        titulo.setFont(new Font("Consolas", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titulo);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Campos de entrada estilizados
        JTextField filasField = crearCampoMinecraft(String.valueOf(laberinto.getFilas()));
        JTextField columnasField = crearCampoMinecraft(String.valueOf(laberinto.getColumnas()));
        
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
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        // Procesar resultado
        if (resultado[0] == 1) {
            try {
                int filas = Integer.parseInt(filasField.getText().trim());
                int columnas = Integer.parseInt(columnasField.getText().trim());
                
                // Validar dimensiones
                if (filas < 5 || columnas < 5) {
                    JOptionPane.showMessageDialog(this, 
                        "Las dimensiones deben ser de al menos 5x5", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return solicitarDimensionesNuevo();
                }
                
                if (filas > 50 || columnas > 50) {
                    int confirmar = JOptionPane.showConfirmDialog(this,
                        "Laberintos mayores a 50x50 pueden ser lentos.\n¿Desea continuar?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (confirmar != JOptionPane.YES_OPTION) {
                        return solicitarDimensionesNuevo();
                    }
                }
                
                return new int[]{filas, columnas};
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese números válidos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return solicitarDimensionesNuevo();
            }
        }
        
        return null; // Usuario canceló
    }
    
    private JTextField crearCampoMinecraft(String valorInicial) {
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
    
    private JButton crearBotonMinecraft(String texto, Color color) {
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
    
    private void limpiarLaberinto() {
        System.out.println("Limpiando laberinto..."); // Debug
        laberinto.limpiar();
        panelLaberinto.limpiarResultado();
        limpiarEstadisticas();
        lblEstado.setText("Laberinto limpiado");
        repaint();
    }
    
    private void resolver() {
        if (panelLaberinto.isAnimacionEnProgreso()) {
            JOptionPane.showMessageDialog(this, 
                "Ya hay una animación en progreso", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String algoritmoSeleccionado = (String) comboAlgoritmos.getSelectedItem();
        ResultadoEjecucion resultado = null;
        
        // Cambiar estado de botones
        btnResolver.setEnabled(false);
        btnDetener.setEnabled(true);
        lblEstado.setText("Resolviendo...");
        
        // Crear copia del laberinto para no modificar el original
        Laberinto copiaLaberinto = laberinto.clonar();
        
        // Medir tiempo en nanosegundos
        long tiempoInicio = System.nanoTime();
        
        // Ejecutar el algoritmo seleccionado
        if (algoritmoSeleccionado.contains("BFS")) {
            BFS bfs = new BFS(copiaLaberinto);
            resultado = bfs.resolver();
        } else if (algoritmoSeleccionado.contains("DFS")) {
            DFS dfs = new DFS(copiaLaberinto);
            resultado = dfs.resolver();
        } else if (algoritmoSeleccionado.equals("Recursivo 2 direcciones")) {
            RecursivoDosDirecciones r2d = new RecursivoDosDirecciones(copiaLaberinto);
            resultado = r2d.resolver();
        } else if (algoritmoSeleccionado.equals("Recursivo 4 direcciones")) {
            RecursivoCuatroDirecciones r4d = new RecursivoCuatroDirecciones(copiaLaberinto);
            resultado = r4d.resolver();
        } else if (algoritmoSeleccionado.contains("backtracking")) {
            RecursivoBacktracking rb = new RecursivoBacktracking(copiaLaberinto);
            resultado = rb.resolver();
        }
        
        long tiempoFin = System.nanoTime();
        
        if (resultado != null) {
            // Actualizar tiempo en nanosegundos
            resultado.setTiempoEjecucionNs(tiempoFin - tiempoInicio);
            
            // Actualizar estadísticas
            actualizarEstadisticas(resultado);
            
            // Animar la solución
            panelLaberinto.animarSolucion(resultado);
            
            // Guardar en CSV
            if (resultado.isEncontroSolucion()) {
                guardador.guardarResultado(resultado, laberinto.getFilas(), laberinto.getColumnas());
                lblEstado.setText("Solución encontrada");
            } else {
                lblEstado.setText("Sin solución");
            }
            
            // Restaurar botones después de la animación
            Timer timer = new Timer(3000, e -> {
                btnResolver.setEnabled(true);
                btnDetener.setEnabled(false);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void compararTodos() {
        // Verificar que hay inicio y fin definidos
        if (laberinto.getInicio() == null || laberinto.getFin() == null) {
            JOptionPane.showMessageDialog(this, 
                "Por favor defina el punto de inicio (A) y fin (B) antes de comparar", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Deshabilitar botones durante la comparación
        btnResolver.setEnabled(false);
        btnDetener.setEnabled(true);
        
        // Thread para ejecutar las animaciones secuencialmente
        new Thread(() -> {
            String[] algoritmos = {
                "BFS (Breadth-First Search)",
                "DFS (Depth-First Search)",
                "Recursivo 2 direcciones",
                "Recursivo 4 direcciones",
                "Recursivo 4 direcciones con backtracking"
            };
            
            List<ResultadoEjecucion> todosResultados = new ArrayList<>();
            
            for (int i = 0; i < algoritmos.length; i++) {
                final String algoritmoActual = algoritmos[i];
                final int indice = i;
                
                try {
                    // Mostrar mensaje de inicio
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "Ejecutando algoritmo:\n" + algoritmoActual,
                            "Comparación de Algoritmos",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                    // Actualizar UI
                    SwingUtilities.invokeAndWait(() -> {
                        comboAlgoritmos.setSelectedItem(algoritmoActual);
                        lblEstado.setText("Resolviendo: " + algoritmoActual.split(" ")[0]);
                        panelLaberinto.limpiarResultado();
                    });
                    
                    // Pequeña pausa para que se vea el cambio
                    Thread.sleep(500);
                    
                    // Ejecutar algoritmo
                    Laberinto copiaLaberinto = laberinto.clonar();
                    ResultadoEjecucion resultado = null;
                    
                    long tiempoInicio = System.nanoTime();
                    
                    if (algoritmoActual.contains("BFS")) {
                        resultado = new BFS(copiaLaberinto).resolver();
                    } else if (algoritmoActual.contains("DFS")) {
                        resultado = new DFS(copiaLaberinto).resolver();
                    } else if (algoritmoActual.equals("Recursivo 2 direcciones")) {
                        resultado = new RecursivoDosDirecciones(copiaLaberinto).resolver();
                    } else if (algoritmoActual.equals("Recursivo 4 direcciones")) {
                        resultado = new RecursivoCuatroDirecciones(copiaLaberinto).resolver();
                    } else if (algoritmoActual.contains("backtracking")) {
                        resultado = new RecursivoBacktracking(copiaLaberinto).resolver();
                    }
                    
                    long tiempoFin = System.nanoTime();
                    
                    if (resultado != null) {
                        resultado.setTiempoEjecucionNs(tiempoFin - tiempoInicio);
                        todosResultados.add(resultado);
                        
                        // Actualizar estadísticas
                        final ResultadoEjecucion resultadoFinal = resultado;
                        SwingUtilities.invokeLater(() -> {
                            actualizarEstadisticas(resultadoFinal);
                        });
                        
                        // Mostrar si encontró solución o no
                        final boolean encontroSolucion = resultado.isEncontroSolucion();
                        SwingUtilities.invokeLater(() -> {
                            if (!encontroSolucion) {
                                JOptionPane.showMessageDialog(this,
                                    algoritmoActual + "\nNo encontró solución",
                                    "Sin solución",
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        });
                        
                        // Animar la solución solo si la encontró
                        if (encontroSolucion) {
                            panelLaberinto.animarSolucion(resultado);
                            
                            // Esperar a que termine la animación
                            Thread.sleep(3000);
                            
                            // Guardar resultado
                            guardador.guardarResultado(resultado, laberinto.getFilas(), laberinto.getColumnas());
                        } else {
                            // Espera menor si no hay solución
                            Thread.sleep(1000);
                        }
                    }
                    
                    // Si no es el último algoritmo, preparar para el siguiente
                    if (indice < algoritmos.length - 1) {
                        SwingUtilities.invokeLater(() -> {
                            panelLaberinto.limpiarResultado();
                            JOptionPane.showMessageDialog(this,
                                "Algoritmo completado.\nContinuando con el siguiente...",
                                "Siguiente algoritmo",
                                JOptionPane.INFORMATION_MESSAGE);
                        });
                        Thread.sleep(1000);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Al finalizar todas las comparaciones
            SwingUtilities.invokeLater(() -> {
                // Mensaje final
                JOptionPane.showMessageDialog(this,
                    "Comparación completada.\nMostrando resultados...",
                    "Fin de la comparación",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Encontrar el mejor resultado
                ResultadoEjecucion mejor = null;
                for (ResultadoEjecucion r : todosResultados) {
                    if (r.isEncontroSolucion()) {
                        if (mejor == null || r.getLongitudCamino() < mejor.getLongitudCamino()) {
                            mejor = r;
                        }
                    }
                }
                
                // Mostrar ventana de comparación
                mostrarVentanaComparacion(todosResultados);
                
                // Restaurar botones
                btnResolver.setEnabled(true);
                btnDetener.setEnabled(false);
                lblEstado.setText("Comparación completada");
            });
            
        }).start();
    }
    
    private void mostrarEstadisticas() {
        // Verificar si hay registros guardados
        if (!guardador.hayRegistros()) {
            JOptionPane.showMessageDialog(this, 
                "No hay estadísticas disponibles.\n" +
                "Debe ejecutar al menos un algoritmo antes de ver las estadísticas.", 
                "Sin datos", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        VentanaEstadisticas ventana = new VentanaEstadisticas(this);
        ventana.setVisible(true);
    }
    
    private void mostrarGraficos() {
        // Verificar si hay registros guardados
        if (!guardador.hayRegistros()) {
            JOptionPane.showMessageDialog(this, 
                "No hay datos disponibles para graficar.\n" +
                "Debe ejecutar al menos un algoritmo antes de ver los gráficos.", 
                "Sin datos", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<ResultadoEjecucion> resultados = ejecutarTodosLosAlgoritmos();
        
        if (!resultados.isEmpty()) {
            VentanaGraficos ventanaGraficos = new VentanaGraficos(this, resultados);
            ventanaGraficos.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "No hay resultados para mostrar", 
                "Sin datos", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void mostrarAcercaDe() {
        String mensaje = "Resolvedor de Laberintos - Minecraft Edition\n\n" +
                        "Desarrollado por:\n" +
                        "- Daniel Sánchez\n" +
                        "- Joey Díaz\n" +
                        "- Daniel Durán\n" +
                        "- Nelson Villalta\n\n" +
                        "Universidad Politécnica Salesiana\n" +
                        "Estructura de Datos - 2025";
        
        JOptionPane.showMessageDialog(this, mensaje, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void nuevoLaberinto() {
        String filasStr = JOptionPane.showInputDialog(this, 
            "Ingrese número de filas (mayor a 4):", 
            "Nuevo Laberinto", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (filasStr == null) return;
        
        String columnasStr = JOptionPane.showInputDialog(this, 
            "Ingrese número de columnas (mayor a 4):", 
            "Nuevo Laberinto", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (columnasStr == null) return;
        
        try {
            int filas = Integer.parseInt(filasStr.trim());
            int columnas = Integer.parseInt(columnasStr.trim());
            
            if (filas > 4 && columnas > 4) {
                this.laberinto = new Laberinto(filas, columnas);
                this.laberinto.generarLaberintoSimple();
                panelLaberinto.setLaberinto(this.laberinto);
                limpiarEstadisticas();
                
                // Actualizar información en el panel superior
                Component[] components = ((JPanel)getContentPane().getComponent(0)).getComponents();
                for (Component comp : components) {
                    if (comp instanceof JLabel && ((JLabel)comp).getText().contains("Laberinto:")) {
                        ((JLabel)comp).setText(String.format("Laberinto: %d x %d", filas, columnas));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Las dimensiones deben ser mayores a 4", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese números válidos", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<ResultadoEjecucion> ejecutarTodosLosAlgoritmos() {
        List<ResultadoEjecucion> resultados = new ArrayList<>();
        
        String[] algoritmos = {
            "BFS (Breadth-First Search)",
            "DFS (Depth-First Search)",
            "Recursivo 2 direcciones",
            "Recursivo 4 direcciones",
            "Recursivo 4 direcciones con backtracking"
        };
        
        for (String algoritmo : algoritmos) {
            comboAlgoritmos.setSelectedItem(algoritmo);
            
            Laberinto copiaLaberinto = laberinto.clonar();
            ResultadoEjecucion resultado = null;
            
            long tiempoInicio = System.nanoTime();
            
            if (algoritmo.contains("BFS")) {
                resultado = new BFS(copiaLaberinto).resolver();
            } else if (algoritmo.contains("DFS")) {
                resultado = new DFS(copiaLaberinto).resolver();
            } else if (algoritmo.equals("Recursivo 2 direcciones")) {
                resultado = new RecursivoDosDirecciones(copiaLaberinto).resolver();
            } else if (algoritmo.equals("Recursivo 4 direcciones")) {
                resultado = new RecursivoCuatroDirecciones(copiaLaberinto).resolver();
            } else if (algoritmo.contains("backtracking")) {
                resultado = new RecursivoBacktracking(copiaLaberinto).resolver();
            }
            
            long tiempoFin = System.nanoTime();
            
            if (resultado != null) {
                resultado.setTiempoEjecucionNs(tiempoFin - tiempoInicio);
                resultados.add(resultado);
                
                if (resultado.isEncontroSolucion()) {
                    guardador.guardarResultado(resultado, laberinto.getFilas(), laberinto.getColumnas());
                }
            }
        }
        
        return resultados;
    }
    
    private void mostrarVentanaComparacion(List<ResultadoEjecucion> resultados) {
        VentanaComparacion ventana = new VentanaComparacion(this, resultados);
        ventana.setVisible(true);
    }
    
    private void actualizarEstadisticas(ResultadoEjecucion resultado) {
        lblTiempo.setText(resultado.getTiempoEjecucion() + " ms");
        lblCeldas.setText(String.valueOf(resultado.getCeldasVisitadas()));
        lblLongitud.setText(resultado.isEncontroSolucion() ? 
            String.valueOf(resultado.getLongitudCamino()) : "No encontrado");
    }
    
    private void salirPrograma() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea salir del programa?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void limpiarEstadisticas() {
        lblTiempo.setText("-- ms");
        lblCeldas.setText("--");
        lblLongitud.setText("--");
        lblEstado.setText("Listo");
    }
}