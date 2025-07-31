package views;

import model.ResultadoEjecucion;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Ventana para comparar resultados de todos los algoritmos
 */
public class VentanaComparacion extends JDialog {
    
    public VentanaComparacion(Frame parent, List<ResultadoEjecucion> resultados) {
        super(parent, "Comparación de Algoritmos", true);
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(139, 195, 74));
        JLabel titulo = new JLabel("COMPARACIÓN DE ALGORITMOS");
        titulo.setFont(new Font("Consolas", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        panelTitulo.add(titulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Crear tabla con resultados
        String[] columnas = {"Algoritmo", "Tiempo (ms)", "Celdas Visitadas", 
                           "Longitud Camino", "Encontró Solución"};
        Object[][] datos = new Object[resultados.size()][5];
        
        for (int i = 0; i < resultados.size(); i++) {
            ResultadoEjecucion r = resultados.get(i);
            datos[i][0] = r.getNombreAlgoritmo();
            datos[i][1] = r.getTiempoEjecucion();
            datos[i][2] = r.getCeldasVisitadas();
            datos[i][3] = r.isEncontroSolucion() ? r.getLongitudCamino() : "N/A";
            datos[i][4] = r.isEncontroSolucion() ? "Sí" : "No";
        }
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(new Color(101, 67, 33));
        tabla.getTableHeader().setForeground(Color.WHITE);
        
        // Centrar el contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);
        
        // Panel inferior con botón cerrar
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(240, 240, 240));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.addActionListener(e -> dispose());
        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);
        
        setSize(700, 400);
        setLocationRelativeTo(parent);
    }
}