package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo Recursivo con 4 direcciones
 * VERSIÓN CORREGIDA: Implementa interfaz y usa nanosegundos
 */
public class RecursivoCuatroDirecciones implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    private boolean encontrado;
    private String nombre;
    
    public RecursivoCuatroDirecciones(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.nombre = "Recursivo 4 direcciones";
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
        
        // Explorar en 4 direcciones
        int filaActual = actual.getFila();
        int colActual = actual.getColumna();
        
        // Direcciones: arriba, derecha, abajo, izquierda
        int[] dirFilas = {-1, 0, 1, 0};
        int[] dirColumnas = {0, 1, 0, -1};
        
        for (int i = 0; i < 4; i++) {
            int nuevaFila = filaActual + dirFilas[i];
            int nuevaCol = colActual + dirColumnas[i];
            
            if (laberinto.esCaminoLibre(nuevaFila, nuevaCol)) {
                Celda vecino = laberinto.getCelda(nuevaFila, nuevaCol);
                if (!vecino.isVisitada()) {
                    buscarRecursivo(vecino, fin);
                }
            }
        }
        
        // Backtrack: quitar del camino actual si no encontramos solución
        if (!encontrado) {
            caminoActual.remove(caminoActual.size() - 1);
        }
    }
}