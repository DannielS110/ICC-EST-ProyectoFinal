package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo BFS (Búsqueda en Amplitud)
 * Actualizado para usar nanosegundos e implementar interfaz
 */
public class BFS implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private Map<Celda, Celda> padres;
    private int celdasVisitadas;
    private String nombre;
    
    public BFS(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.padres = new HashMap<>();
        this.celdasVisitadas = 0;
        this.nombre="BFS (Breadth-First Search)";
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
        
        Queue<Celda> cola = new LinkedList<>();
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();
        
        cola.offer(inicio);
        inicio.setVisitada(true);
        celdasVisitadas++;
        
        boolean encontrado = false;
        
        while (!cola.isEmpty() && !encontrado) {
            Celda actual = cola.poll();
            
            if (actual.equals(fin)) {
                encontrado = true;
                break;
            }
            
            List<Celda> vecinos = laberinto.getVecinosNoVisitados(actual);
            
            for (Celda vecino : vecinos) {
                vecino.setVisitada(true);
                celdasVisitadas++;
                padres.put(vecino, actual);
                cola.offer(vecino);
            }
        }
        
        if (encontrado) {
            List<Celda> camino = reconstruirCamino(fin);
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