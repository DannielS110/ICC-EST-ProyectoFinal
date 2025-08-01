package views;

import data.GuardadorDatos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;

/**
 * Ventana que muestra estadísticas históricas desde el CSV
 * Versión completa con gráficos integrados y validaciones
 */
public class VentanaEstadisticas extends JDialog {
    private GuardadorDatos guardador;
    private List<String[]> registros;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    
    public VentanaEstadisticas(Frame parent) {
        super(parent, "Estadísticas Históricas", true);
        this.guardador = new GuardadorDatos();
        
        // Verificar si hay datos antes de continuar
        if (!guardador.hayRegistros()) {
            JOptionPane.showMessageDialog(parent, 
                "No hay estadísticas disponibles.\n" +
                "Debe ejecutar al menos un algoritmo antes de ver las estadísticas.", 
                "Sin datos", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }
        
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }
    
    private void configurarVentana() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));
        setSize(1200, 700);
        setLocationRelativeTo(getParent());
    }
    
    private void inicializarComponentes() {
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(139, 195, 74));
        JLabel titulo = new JLabel("ESTADÍSTICAS HISTÓRICAS");
        titulo.setFont(new Font("Consolas", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        panelTitulo.add(titulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Verificar si hay datos antes de crear tabs
        registros = guardador.leerTodos();
        if (registros.isEmpty()) {
            JLabel lblNoData = new JLabel("No hay datos para mostrar");
            lblNoData.setHorizontalAlignment(JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 18));
            add(lblNoData, BorderLayout.CENTER);
        } else {
            // Panel principal con tabs
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
            
            // Tab de tabla
            tabbedPane.addTab("Tabla de Resultados", crearPanelTabla());
            
            // Tab de gráficos solo si hay datos
            tabbedPane.addTab("Gráficos Comparativos", crearPanelGraficos());
            
            add(tabbedPane, BorderLayout.CENTER);
        }
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(240, 240, 240));
        
        JButton btnLimpiar = new JButton("Limpiar Historial");
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 14));
        btnLimpiar.setBackground(new Color(255, 100, 100));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarHistorial());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(100, 200, 100));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarDatos());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(150, 150, 150));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear tabla
        String[] columnas = {"ID", "Algoritmo", "Filas", "Columnas", "Tiempo (ns)", 
                           "Celdas", "Longitud", "Resuelto", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.setSelectionBackground(new Color(184, 207, 229));
        
        // Configurar header con mejor visibilidad
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(60, 60, 60));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 30));
        header.setReorderingAllowed(false);
        
        // Renderer personalizado para el header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(60, 60, 60));
                setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 14));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                return this;
            }
        };
        
        // Aplicar renderer a todas las columnas del header
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(8).setPreferredWidth(150);
        
        // Centrar contenido de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            if (i != 1 && i != 8) { // No centrar algoritmo y fecha
                tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scroll, BorderLayout.CENTER);
        
        // Panel de resumen
        JPanel panelResumen = crearPanelResumen();
        panel.add(panelResumen, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblResumen = new JLabel("Resumen: ");
        lblResumen.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblResumen);
        
        return panel;
    }
    
    private JPanel crearPanelGraficos() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Solo crear gráficos si hay datos
        if (registros != null && !registros.isEmpty()) {
            // Gráfico 1: Tiempo de ejecución por algoritmo
            panel.add(new ChartPanel(crearGraficoTiempos()));
            
            // Gráfico 2: Celdas visitadas por algoritmo
            panel.add(new ChartPanel(crearGraficoCeldas()));
            
            // Gráfico 3: Longitud del camino por algoritmo
            panel.add(new ChartPanel(crearGraficoLongitud()));
            
            // Gráfico 4: Tasa de éxito por algoritmo
            panel.add(new ChartPanel(crearGraficoExito()));
        } else {
            JLabel lblNoData = new JLabel("No hay datos para graficar");
            lblNoData.setHorizontalAlignment(JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(lblNoData);
        }
        
        return panel;
    }
    
    private JFreeChart crearGraficoTiempos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Agrupar por algoritmo y calcular promedio
        Map<String, Double> sumaTiempos = new HashMap<>();
        Map<String, Integer> contadores = new HashMap<>();
        
        for (String[] registro : registros) {
            String algoritmo = registro[1];
            double tiempoMs = Long.parseLong(registro[4]) / 1_000_000.0; // Convertir a ms
            
            sumaTiempos.put(algoritmo, sumaTiempos.getOrDefault(algoritmo, 0.0) + tiempoMs);
            contadores.put(algoritmo, contadores.getOrDefault(algoritmo, 0) + 1);
        }
        
        // Calcular promedios
        for (String algoritmo : sumaTiempos.keySet()) {
            double promedio = sumaTiempos.get(algoritmo) / contadores.get(algoritmo);
            dataset.addValue(promedio, "Tiempo Promedio", algoritmo);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Tiempo de Ejecución Promedio por Algoritmo",
            "Algoritmo",
            "Tiempo (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGrafico(chart, new Color(46, 125, 50));
        return chart;
    }
    
    private JFreeChart crearGraficoCeldas() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Agrupar por algoritmo y calcular promedio
        Map<String, Integer> sumaCeldas = new HashMap<>();
        Map<String, Integer> contadores = new HashMap<>();
        
        for (String[] registro : registros) {
            String algoritmo = registro[1];
            int celdas = Integer.parseInt(registro[5]);
            
            sumaCeldas.put(algoritmo, sumaCeldas.getOrDefault(algoritmo, 0) + celdas);
            contadores.put(algoritmo, contadores.getOrDefault(algoritmo, 0) + 1);
        }
        
        // Calcular promedios
        for (String algoritmo : sumaCeldas.keySet()) {
            double promedio = (double) sumaCeldas.get(algoritmo) / contadores.get(algoritmo);
            dataset.addValue(promedio, "Celdas Promedio", algoritmo);
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "Celdas Visitadas Promedio por Algoritmo",
            "Algoritmo",
            "Número de Celdas",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGrafico(chart, new Color(33, 150, 243));
        return chart;
    }
    
    private JFreeChart crearGraficoLongitud() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Agrupar por algoritmo y calcular promedio solo para casos exitosos
        Map<String, Integer> sumaLongitud = new HashMap<>();
        Map<String, Integer> contadores = new HashMap<>();
        
        for (String[] registro : registros) {
            if (registro[7].equals("SI")) {
                String algoritmo = registro[1];
                int longitud = Integer.parseInt(registro[6]);
                
                sumaLongitud.put(algoritmo, sumaLongitud.getOrDefault(algoritmo, 0) + longitud);
                contadores.put(algoritmo, contadores.getOrDefault(algoritmo, 0) + 1);
            }
        }
        
        // Calcular promedios
        for (String algoritmo : sumaLongitud.keySet()) {
            double promedio = (double) sumaLongitud.get(algoritmo) / contadores.get(algoritmo);
            dataset.addValue(promedio, "Longitud Promedio", algoritmo);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Longitud Promedio del Camino Encontrado",
            "Algoritmo",
            "Número de Pasos",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGrafico(chart, new Color(255, 152, 0));
        return chart;
    }
    
    private JFreeChart crearGraficoExito() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Contar éxitos por algoritmo
        Map<String, Integer> exitos = new HashMap<>();
        Map<String, Integer> totales = new HashMap<>();
        
        for (String[] registro : registros) {
            String algoritmo = registro[1];
            totales.put(algoritmo, totales.getOrDefault(algoritmo, 0) + 1);
            if (registro[7].equals("SI")) {
                exitos.put(algoritmo, exitos.getOrDefault(algoritmo, 0) + 1);
            }
        }
        
        for (String algoritmo : totales.keySet()) {
            double tasaExito = (exitos.getOrDefault(algoritmo, 0) * 100.0) / totales.get(algoritmo);
            dataset.addValue(tasaExito, "Tasa de Éxito", algoritmo);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Tasa de Éxito por Algoritmo",
            "Algoritmo",
            "Porcentaje de Éxito (%)",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGrafico(chart, new Color(76, 175, 80));
        return chart;
    }
    
    private void personalizarGrafico(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 248, 248));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);
        
        // Configurar el color del renderer
        plot.getRenderer().setSeriesPaint(0, color);
        
        // Si es BarRenderer, configuraciones adicionales
        if (plot.getRenderer() instanceof BarRenderer) {
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setShadowVisible(false);
            renderer.setMaximumBarWidth(0.1);
        }
        
        // Para gráficos de línea
        if (plot.getRenderer() instanceof LineAndShapeRenderer) {
            LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) plot.getRenderer();
            lineRenderer.setSeriesStroke(0, new BasicStroke(3.0f));
            lineRenderer.setSeriesShapesVisible(0, true);
            lineRenderer.setSeriesShapesFilled(0, true);
        }
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        // Agregar formato al eje Y
        if (plot.getRangeAxis() instanceof NumberAxis) {
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
    }
    
    private void cargarDatos() {
        registros = guardador.leerTodos();
        
        if (modeloTabla != null) {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Llenar tabla
            for (String[] registro : registros) {
                Object[] fila = new Object[9];
                fila[0] = registro[0]; // ID
                fila[1] = registro[1]; // Algoritmo
                fila[2] = registro[2]; // Filas
                fila[3] = registro[3]; // Columnas
                fila[4] = registro[4]; // Tiempo
                fila[5] = registro[5]; // Celdas
                fila[6] = registro[6]; // Longitud
                fila[7] = registro[7]; // Resuelto
                fila[8] = registro[10]; // Fecha
                modeloTabla.addRow(fila);
            }
            
            // Actualizar resumen
            actualizarResumen();
        }
        
        // Actualizar gráficos
        repaint();
    }
    
    private void actualizarResumen() {
        int total = registros.size();
        int exitosos = 0;
        for (String[] registro : registros) {
            if (registro[7].equals("SI")) {
                exitosos++;
            }
        }
        
        Component comp = getContentPane().getComponent(1);
        if (comp instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) comp;
            Component tabComponent = tabbedPane.getComponentAt(0);
            if (tabComponent instanceof JPanel) {
                JPanel panelTabla = (JPanel) tabComponent;
                if (panelTabla.getComponentCount() > 1) {
                    Component resumenComponent = panelTabla.getComponent(1);
                    if (resumenComponent instanceof JPanel) {
                        JPanel panelResumen = (JPanel) resumenComponent;
                        panelResumen.removeAll();
                        
                        JLabel lblResumen = new JLabel(String.format(
                            "Total de ejecuciones: %d | Exitosas: %d | Tasa de éxito: %.1f%%",
                            total, exitosos, total > 0 ? (exitosos * 100.0) / total : 0
                        ));
                        lblResumen.setFont(new Font("Arial", Font.BOLD, 14));
                        panelResumen.add(lblResumen);
                        panelResumen.revalidate();
                        panelResumen.repaint();
                    }
                }
            }
        }
    }
    
    private void limpiarHistorial() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de limpiar todo el historial?\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            guardador.limpiarRegistros();
            JOptionPane.showMessageDialog(this, 
                "Historial limpiado exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}