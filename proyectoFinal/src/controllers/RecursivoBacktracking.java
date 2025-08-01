package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo Recursivo con Backtracking mejorado
 * VERSIÓN CORREGIDA: Muestra todas las celdas visitadas
 */
public class RecursivoBacktracking implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    private List<Celda> ordenVisitas;
    private Set<Celda> todasLasVisitadas; // Para mantener registro de TODAS las visitadas
    
    // PROGRAMACIÓN DINÁMICA: Cache para memoización
    private Map<String, Integer> memo;
    private int cachesUtilizados;
    private String nombre;
    
    public RecursivoBacktracking(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.memo = new HashMap<>();
        this.cachesUtilizados = 0;
        this.nombre = "Recursivo 4 direcciones con backtracking";
        this.ordenVisitas = new ArrayList<>();
        this.todasLasVisitadas = new HashSet<>();
    }
    
    @Override
    public String getNombre() {
        return nombre;
    }
    
    private String generarClave(int fila, int col, Celda destino) {
        return fila + "," + col + "->" + destino.getFila() + "," + destino.getColumna();
    }
    
    @Override
    public ResultadoEjecucion resolver() {
        ResultadoEjecucion resultado = new ResultadoEjecucion(nombre);
        long tiempoInicio = System.nanoTime();
        
        // Reiniciar variables
        laberinto.reiniciarVisitadas();
        caminoActual = new ArrayList<>();
        mejorCamino = null;
        celdasVisitadas = 0;
        ordenVisitas.clear();
        todasLasVisitadas.clear();
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
            
            // Marcar TODAS las celdas visitadas durante la búsqueda
            for (Celda celda : todasLasVisitadas) {
                celda.setVisitada(true);
            }
            
            // Marcar el camino final
            for (Celda celda : mejorCamino) {
                celda.setEnCamino(true);
            }
        }
        
        resultado.setCeldasVisitadas(celdasVisitadas);
        resultado.setOrdenVisitas(new ArrayList<>(ordenVisitas));
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
        ordenVisitas.add(actual);
        todasLasVisitadas.add(actual); // Guardar en el conjunto de todas las visitadas
        
        // Verificar si llegamos al fin
        if (actual.equals(fin)) {
            // Si es el primer camino o es más corto que el mejor actual
            if (mejorCamino == null || caminoActual.size() < mejorCamino.size()) {
                mejorCamino = new ArrayList<>(caminoActual);
                
                // PROGRAMACIÓN DINÁMICA: Guardar en cache la distancia mínima
                String clave = generarClave(actual.getFila(), actual.getColumna(), fin);
                memo.put(clave, 0);
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
                    actual.setVisitada(false); // Solo para el backtracking local
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
        
        // Backtrack: quitar del camino pero NO del conjunto de visitadas
        caminoActual.remove(caminoActual.size() - 1);
        actual.setVisitada(false); // Solo para permitir otras rutas
    }
}