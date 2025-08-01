package views;

import model.ResultadoEjecucion;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.*;

/**
 * Ventana para mostrar gráficos estadísticos de los algoritmos
 */
public class VentanaGraficos extends JDialog {
    private List<ResultadoEjecucion> resultados;
    
    public VentanaGraficos(Frame parent, List<ResultadoEjecucion> resultados) {
        super(parent, "Análisis Gráfico de Algoritmos", true);
        this.resultados = resultados;
        
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(new Color(240, 240, 240));
    }
    
    private void inicializarComponentes() {
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(139, 195, 74));
        JLabel titulo = new JLabel("ANÁLISIS COMPARATIVO DE ALGORITMOS");
        titulo.setFont(new Font("Consolas", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        panelTitulo.add(titulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel con pestañas para diferentes gráficos
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Agregar diferentes tipos de gráficos
        tabbedPane.addTab("Tiempo de Ejecución", crearPanelTiempo());
        tabbedPane.addTab("Celdas Visitadas", crearPanelCeldas());
        tabbedPane.addTab("Longitud del Camino", crearPanelLongitud());
        tabbedPane.addTab("Comparación General", crearPanelComparacion());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(240, 240, 240));
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(150, 150, 150));
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTiempo() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (ResultadoEjecucion r : resultados) {
            if (r.isEncontroSolucion()) {
                // Convertir nanosegundos a microsegundos para mejor visualización
                double tiempoMicros = r.getTiempoEjecucionNs() / 1000.0;
                dataset.addValue(tiempoMicros, "Tiempo", r.getNombreAlgoritmo());
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Tiempo de Ejecución por Algoritmo",
            "Algoritmo",
            "Tiempo (microsegundos)",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGraficoBarras(chart, new Color(46, 125, 50));
        
        return new ChartPanel(chart);
    }
    
    private JPanel crearPanelCeldas() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (ResultadoEjecucion r : resultados) {
            dataset.addValue(r.getCeldasVisitadas(), "Celdas", r.getNombreAlgoritmo());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "Celdas Visitadas por Algoritmo",
            "Algoritmo",
            "Número de Celdas",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGraficoLineas(chart);
        
        return new ChartPanel(chart);
    }
    
    private JPanel crearPanelLongitud() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (ResultadoEjecucion r : resultados) {
            if (r.isEncontroSolucion()) {
                dataset.addValue(r.getLongitudCamino(), "Longitud", r.getNombreAlgoritmo());
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Longitud del Camino Encontrado",
            "Algoritmo",
            "Número de Pasos",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        personalizarGraficoBarras(chart, new Color(255, 152, 0));
        
        return new ChartPanel(chart);
    }
    
    private JPanel crearPanelComparacion() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (ResultadoEjecucion r : resultados) {
            if (r.isEncontroSolucion()) {
                // Normalizar valores para comparación
                double tiempoNormalizado = r.getTiempoEjecucionNs() / 1000000.0; // a ms
                double celdasNormalizadas = r.getCeldasVisitadas() / 10.0;
                double longitudNormalizada = r.getLongitudCamino();
                
                dataset.addValue(tiempoNormalizado, "Tiempo (ms)", r.getNombreAlgoritmo());
                dataset.addValue(celdasNormalizadas, "Celdas/10", r.getNombreAlgoritmo());
                dataset.addValue(longitudNormalizada, "Longitud", r.getNombreAlgoritmo());
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Comparación General de Métricas",
            "Algoritmo",
            "Valor Normalizado",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        personalizarGraficoMultiple(chart);
        
        return new ChartPanel(chart);
    }
    
    private void personalizarGraficoBarras(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 248, 248));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, color);
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.1);
        
        // Rotar etiquetas del eje X
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        // Formato del eje Y
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }
    
    private void personalizarGraficoLineas(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 248, 248));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);
        
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(33, 150, 243));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        // Formato del eje Y
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }
    
    private void personalizarGraficoMultiple(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 248, 248));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        // Colores para cada serie
        renderer.setSeriesPaint(0, new Color(244, 67, 54));  // Tiempo - Rojo
        renderer.setSeriesPaint(1, new Color(33, 150, 243)); // Celdas - Azul
        renderer.setSeriesPaint(2, new Color(76, 175, 80));  // Longitud - Verde
        
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.1);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        // Formato del eje Y
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }
}