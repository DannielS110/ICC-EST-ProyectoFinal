package controllers;

import model.*;
import java.util.*;

/**
 * Recursivo 2 direcciones - Rígido y metódico
 * "Solo derecha y abajo, lo justo y necesario"
 */
public class RecursivoDosDirecciones implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    private String nombre;
    private List<Celda> ordenVisitas;
    
    public RecursivoDosDirecciones(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.nombre = "Recursivo 2 direcciones";
        this.ordenVisitas = new ArrayList<>();
    }
    
    @Override
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public ResultadoEjecucion resolver() {
        ResultadoEjecucion resultado = new ResultadoEjecucion(nombre);
        long tiempoInicio = System.nanoTime();
        
        // Reiniciar variables
        laberinto.reiniciarVisitadas();
        mejorCamino = null;
        celdasVisitadas = 0;
        ordenVisitas.clear();
        
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();
        
        // Buscar camino solo yendo derecha o abajo
        List<Celda> caminoActual = new ArrayList<>();
        buscarCamino(inicio, fin, caminoActual);
        
        // Establecer resultados
        if (mejorCamino != null) {
            resultado.setCamino(new ArrayList<>(mejorCamino));
            resultado.setEncontroSolucion(true);
            
            // Marcar el camino amarillo
            for (Celda celda : mejorCamino) {
                celda.setEnCamino(true);
            }
        }
        
        resultado.setCeldasVisitadas(celdasVisitadas);
        resultado.setOrdenVisitas(new ArrayList<>(ordenVisitas));
        
        long tiempoNs = System.nanoTime() - tiempoInicio;
        resultado.setTiempoEjecucionNs(tiempoNs);
        
        return resultado;
    }
    
    private void buscarCamino(Celda actual, Celda fin, List<Celda> caminoActual) {
        // Evitar ciclos
        if (caminoActual.contains(actual)) return;
        
        // Marcar visitada (gris)
        if (!actual.isVisitada()) {
            actual.setVisitada(true);
            celdasVisitadas++;
            ordenVisitas.add(actual);
        }
        
        caminoActual.add(actual);
        
        // Si llegamos al fin
        if (actual.equals(fin)) {
            if (mejorCamino == null || caminoActual.size() < mejorCamino.size()) {
                mejorCamino = new ArrayList<>(caminoActual);
            }
            caminoActual.remove(caminoActual.size() - 1);
            return;
        }
        
        int fila = actual.getFila();
        int col = actual.getColumna();
        
        // Solo derecha
        if (laberinto.esCaminoLibre(fila, col + 1)) {
            Celda derecha = laberinto.getCelda(fila, col + 1);
            buscarCamino(derecha, fin, caminoActual);
        }
        
        // Solo abajo
        if (laberinto.esCaminoLibre(fila + 1, col)) {
            Celda abajo = laberinto.getCelda(fila + 1, col);
            buscarCamino(abajo, fin, caminoActual);
        }
        
        caminoActual.remove(caminoActual.size() - 1);
    }
}