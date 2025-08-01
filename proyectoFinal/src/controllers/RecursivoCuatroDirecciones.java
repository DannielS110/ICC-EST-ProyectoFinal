package controllers;

import model.*;
import java.util.*;

/**
 * Recursivo 4 direcciones
 * Primero muestra las visitadas en gris, luego el camino en amarillo,
 * y finalmente pinta todo de amarillo
 */
public class RecursivoCuatroDirecciones implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> caminoFinal;
    private int celdasVisitadas;
    private boolean encontrado;
    private String nombre;
    private List<Celda> ordenVisitas;
    private Set<Celda> todasLasVisitadas;
    
    public RecursivoCuatroDirecciones(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.nombre = "Recursivo 4 direcciones";
        this.ordenVisitas = new ArrayList<>();
        this.todasLasVisitadas = new HashSet<>();
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
        caminoActual = new ArrayList<>();
        caminoFinal = null;
        celdasVisitadas = 0;
        encontrado = false;
        ordenVisitas.clear();
        todasLasVisitadas.clear();
        
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();
        
        // Buscar recursivamente
        buscarRecursivo(inicio, fin);
        
        // Marcar todas las celdas visitadas
        for (Celda celda : todasLasVisitadas) {
            celda.setVisitada(true);
        }
        
        // Establecer resultados
        if (caminoFinal != null) {
            resultado.setCamino(new ArrayList<>(caminoFinal));
            resultado.setEncontroSolucion(true);
            
            // Marcar el camino
            for (Celda celda : caminoFinal) {
                celda.setEnCamino(true);
            }
        }
        
        resultado.setCeldasVisitadas(celdasVisitadas);
        resultado.setOrdenVisitas(new ArrayList<>(ordenVisitas));
        
        long tiempoNs = System.nanoTime() - tiempoInicio;
        resultado.setTiempoEjecucionNs(tiempoNs);
        
        return resultado;
    }
    
    private boolean buscarRecursivo(Celda actual, Celda fin) {
        // Si ya encontramos el camino, no seguir buscando
        if (encontrado) {
            return true;
        }
        
        // Marcar como visitada temporalmente
        actual.setVisitada(true);
        caminoActual.add(actual);
        
        // Registrar en la lista de visitadas si es primera vez
        if (!todasLasVisitadas.contains(actual)) {
            celdasVisitadas++;
            ordenVisitas.add(actual);
            todasLasVisitadas.add(actual);
        }
        
        // Verificar si llegamos al fin
        if (actual.equals(fin)) {
            encontrado = true;
            caminoFinal = new ArrayList<>(caminoActual);
            return true;
        }
        
        // Explorar en 4 direcciones
        int fila = actual.getFila();
        int col = actual.getColumna();
        
        // Direcciones: arriba, derecha, abajo, izquierda
        int[] dirFilas = {-1, 0, 1, 0};
        int[] dirCols = {0, 1, 0, -1};
        
        for (int i = 0; i < 4; i++) {
            int nuevaFila = fila + dirFilas[i];
            int nuevaCol = col + dirCols[i];
            
            if (laberinto.esCaminoLibre(nuevaFila, nuevaCol)) {
                Celda vecino = laberinto.getCelda(nuevaFila, nuevaCol);
                if (!vecino.isVisitada()) {
                    if (buscarRecursivo(vecino, fin)) {
                        return true;
                    }
                }
            }
        }
        
        // Backtrack
        caminoActual.remove(caminoActual.size() - 1);
        actual.setVisitada(false);
        return false;
    }
}