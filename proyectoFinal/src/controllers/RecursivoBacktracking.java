package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo Recursivo con Backtracking mejorado
 * Incluye PROGRAMACIÓN DINÁMICA mediante memoización
 */
public class RecursivoBacktracking implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    
    // PROGRAMACIÓN DINÁMICA: Cache para memoización
    private Map<String, Integer> memo;
    private int cachesUtilizados;
    
    private final String NOMBRE = "Recursivo 4 direcciones con backtracking";
    
    public RecursivoBacktracking(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.memo = new HashMap<>();
        this.cachesUtilizados = 0;
    }
    
    @Override
    public String getNombre() {
        return NOMBRE;
    }
    
    private String generarClave(int fila, int col, Celda destino) {
        return fila + "," + col + "->" + destino.getFila() + "," + destino.getColumna();
    }
    
    @Override
    public ResultadoEjecucion resolver() {
        ResultadoEjecucion resultado = new ResultadoEjecucion(NOMBRE);
        long tiempoInicio = System.nanoTime();
        
        // Reiniciar variables
        laberinto.reiniciarVisitadas();
        caminoActual = new ArrayList<>();
        mejorCamino = null;
        celdasVisitadas = 0;
        memo.clear();
        cachesUtilizados = 0;
        
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();
        
        // Iniciar búsqueda recursiva con backtracking y memoización
        buscarConBacktrackingDP(inicio, fin);
        
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
        resultado.setUsoProgramacionDinamica(true);
        resultado.setCachesUtilizados(cachesUtilizados);
        resultado.setTamanoCache(memo.size());
        
        long tiempoNs = System.nanoTime() - tiempoInicio;
        resultado.setTiempoEjecucionNs(tiempoNs);
        
        return resultado;
    }
    
    private void buscarConBacktrackingDP(Celda actual, Celda fin) {
        // Marcar como visitada y agregar al camino
        actual.setVisitada(true);
        celdasVisitadas++;
        caminoActual.add(actual);
        
        // Verificar si llegamos al fin
        if (actual.equals(fin)) {
            // Si es el primer camino o es más corto que el mejor actual
            if (mejorCamino == null || caminoActual.size() < mejorCamino.size()) {
                mejorCamino = new ArrayList<>(caminoActual);
                
                // PROGRAMACIÓN DINÁMICA: Guardar en cache la distancia mínima
                String clave = generarClave(actual.getFila(), actual.getColumna(), fin);
                memo.put(clave, 0); // Distancia 0 al destino
            }
        } else {
            // PROGRAMACIÓN DINÁMICA: Verificar si ya conocemos la distancia mínima desde aquí
            String clave = generarClave(actual.getFila(), actual.getColumna(), fin);
            if (memo.containsKey(clave)) {
                int distanciaConocida = memo.get(clave);
                cachesUtilizados++;
                
                // Si el camino actual + distancia conocida es peor que el mejor, podar
                if (mejorCamino != null && 
                    caminoActual.size() + distanciaConocida >= mejorCamino.size()) {
                    // Backtrack inmediato
                    caminoActual.remove(caminoActual.size() - 1);
                    actual.setVisitada(false);
                    return;
                }
            }
            
            // Si aún no hemos encontrado ningún camino o
            // el camino actual es más corto que el mejor encontrado
            if (mejorCamino == null || caminoActual.size() < mejorCamino.size()) {
                // Explorar en 4 direcciones
                List<Celda> vecinos = laberinto.getVecinosNoVisitados(actual);
                
                // Ordenar vecinos por distancia Manhattan al fin (heurística)
                vecinos.sort((a, b) -> {
                    int distA = Math.abs(a.getFila() - fin.getFila()) + 
                               Math.abs(a.getColumna() - fin.getColumna());
                    int distB = Math.abs(b.getFila() - fin.getFila()) + 
                               Math.abs(b.getColumna() - fin.getColumna());
                    return Integer.compare(distA, distB);
                });
                
                int mejorDistanciaDesdeAqui = Integer.MAX_VALUE;
                
                for (Celda vecino : vecinos) {
                    int tamanoAntes = mejorCamino != null ? mejorCamino.size() : Integer.MAX_VALUE;
                    buscarConBacktrackingDP(vecino, fin);
                    
                    // Actualizar mejor distancia encontrada desde este punto
                    if (mejorCamino != null && mejorCamino.size() < tamanoAntes) {
                        mejorDistanciaDesdeAqui = mejorCamino.size() - caminoActual.size();
                    }
                }
                
                // PROGRAMACIÓN DINÁMICA: Guardar la mejor distancia encontrada
                if (mejorDistanciaDesdeAqui < Integer.MAX_VALUE) {
                    memo.put(clave, mejorDistanciaDesdeAqui);
                }
            }
        }
        
        // Backtrack: desmarcar y quitar del camino
        caminoActual.remove(caminoActual.size() - 1);
        actual.setVisitada(false);
    }
}