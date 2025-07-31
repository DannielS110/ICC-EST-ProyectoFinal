package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo Recursivo con 4 direcciones
 * Explora en todas las direcciones posibles
 */
public class RecursivoCuatroDirecciones {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    private boolean encontrado;
    
    public RecursivoCuatroDirecciones(Laberinto laberinto) {
        this.laberinto = laberinto;
    }
    
    public ResultadoEjecucion resolver() {
        ResultadoEjecucion resultado = new ResultadoEjecucion("Recursivo 4 Direcciones");
        long tiempoInicio = System.currentTimeMillis();
        
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
        resultado.setTiempoEjecucion(System.currentTimeMillis() - tiempoInicio);
        
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