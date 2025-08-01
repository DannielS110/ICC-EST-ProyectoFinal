package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo DFS (Búsqueda en Profundidad)
 * VERSIÓN CORREGIDA: Implementa interfaz y usa nanosegundos
 */
public class DFS implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private Map<Celda, Celda> padres;
    private int celdasVisitadas;
    private String nombre;
    
    public DFS(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.padres = new HashMap<>();
        this.celdasVisitadas = 0;
        this.nombre = "DFS (Depth-First Search)";
    }
    
    @Override
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public ResultadoEjecucion resolver() {
        ResultadoEjecucion resultado = new ResultadoEjecucion(nombre);
        long tiempoInicio = System.nanoTime(); // CAMBIO: usar nanoTime
        
        laberinto.reiniciarVisitadas();
        padres.clear();
        celdasVisitadas = 0;
        
        Stack<Celda> pila = new Stack<>();
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();
        
        pila.push(inicio);
        inicio.setVisitada(true);
        celdasVisitadas++;
        
        boolean encontrado = false;
        Celda nodoFinal = null;
        
        while (!pila.isEmpty() && !encontrado) {
            Celda actual = pila.pop();
            
            if (actual.equals(fin)) {
                encontrado = true;
                nodoFinal = actual;
                break;
            }
            
            List<Celda> vecinos = laberinto.getVecinosNoVisitados(actual);
            
            for (int i = vecinos.size() - 1; i >= 0; i--) {
                Celda vecino = vecinos.get(i);
                vecino.setVisitada(true);
                celdasVisitadas++;
                padres.put(vecino, actual);
                pila.push(vecino);
            }
        }
        
        if (encontrado && nodoFinal != null) {
            List<Celda> camino = reconstruirCamino(nodoFinal);
            resultado.setCamino(camino);
            resultado.setEncontroSolucion(true);
            
            for (Celda celda : camino) {
                celda.setEnCamino(true);
            }
        }
        
        resultado.setCeldasVisitadas(celdasVisitadas);
        
        // CAMBIO: Calcular tiempo en nanosegundos
        long tiempoNs = System.nanoTime() - tiempoInicio;
        resultado.setTiempoEjecucionNs(tiempoNs);
        
        return resultado;
    }
    
    private List<Celda> reconstruirCamino(Celda fin) {
        List<Celda> camino = new ArrayList<>();
        Celda actual = fin;
        
        while (actual != null) {
            camino.add(0, actual);
            actual = padres.get(actual);
        }
        
        return camino;
    }
}