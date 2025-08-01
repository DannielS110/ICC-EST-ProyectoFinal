package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo Recursivo con 2 direcciones (derecha y abajo)
 * VERSIÓN CORREGIDA: Implementa interfaz y usa nanosegundos
 */
public class RecursivoDosDirecciones implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    private boolean encontrado;
    private String nombre;
    
    public RecursivoDosDirecciones(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.nombre = "Recursivo 2 direcciones";
    }
    
    @Override
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public ResultadoEjecucion resolver() {
        ResultadoEjecucion resultado = new ResultadoEjecucion(nombre);
        long tiempoInicio = System.nanoTime(); // CAMBIO: usar nanoTime
        
        // Reiniciar variables
        laberinto.reiniciarVisitadas();
        caminoActual = new ArrayList<>();
        mejorCamino = null;
        celdasVisitadas = 0;
        encontrado = false;
        
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();
        
        // Iniciar búsqueda recursiva
        buscarRecursivo(inicio, fin);
        
        // Establecer resultados
        if (mejorCamino != null) {
            resultado.setCamino(new ArrayList<>(mejorCamino));
            resultado.setEncontroSolucion(true);
            
            // Marcar el camino
            for (Celda celda : mejorCamino) {
                celda.setEnCamino(true);
            }
        }
        
        resultado.setCeldasVisitadas(celdasVisitadas);
        
        // CAMBIO: Calcular tiempo en nanosegundos
        long tiempoNs = System.nanoTime() - tiempoInicio;
        resultado.setTiempoEjecucionNs(tiempoNs);
        
        return resultado;
    }
    
    private void buscarRecursivo(Celda actual, Celda fin) {
        // Si ya encontramos una solución, no seguir buscando
        if (encontrado) {
            return;
        }
        
        // Marcar como visitada y agregar al camino
        actual.setVisitada(true);
        celdasVisitadas++;
        caminoActual.add(actual);
        
        // Verificar si llegamos al fin
        if (actual.equals(fin)) {
            encontrado = true;
            mejorCamino = new ArrayList<>(caminoActual);
            return;
        }
        
        // Explorar solo 2 direcciones: derecha y abajo
        int filaActual = actual.getFila();
        int colActual = actual.getColumna();
        
        // Derecha
        if (laberinto.esCaminoLibre(filaActual, colActual + 1)) {
            Celda derecha = laberinto.getCelda(filaActual, colActual + 1);
            if (!derecha.isVisitada()) {
                buscarRecursivo(derecha, fin);
            }
        }
        
        // Abajo
        if (laberinto.esCaminoLibre(filaActual + 1, colActual)) {
            Celda abajo = laberinto.getCelda(filaActual + 1, colActual);
            if (!abajo.isVisitada()) {
                buscarRecursivo(abajo, fin);
            }
        }
        
        // Backtrack: quitar del camino actual si no encontramos solución
        if (!encontrado) {
            caminoActual.remove(caminoActual.size() - 1);
        }
    }
}