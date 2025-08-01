package controllers;

import model.*;
import java.util.*;

/**
 * Implementación del algoritmo Recursivo con 4 direcciones
 * Ahora guarda el orden real de visita para animaciones.
 */
public class RecursivoCuatroDirecciones implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private List<Celda> caminoActual;
    private List<Celda> mejorCamino;
    private int celdasVisitadas;
    private boolean encontrado;
    private String nombre;

    // NUEVO: orden real de visita
    private List<Celda> ordenVisitas;

    public RecursivoCuatroDirecciones(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.nombre = "Recursivo 4 direcciones";
        this.ordenVisitas = new ArrayList<>(); // NUEVO
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
        mejorCamino = null;
        celdasVisitadas = 0;
        encontrado = false;
        ordenVisitas.clear(); // NUEVO

        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();

        // Iniciar búsqueda recursiva
        buscarRecursivo(inicio, fin);

        // Establecer resultados
        if (mejorCamino != null) {
            resultado.setCamino(new ArrayList<>(mejorCamino));
            resultado.setEncontroSolucion(true);

            for (Celda celda : mejorCamino) {
                celda.setEnCamino(true);
            }
        }

        resultado.setCeldasVisitadas(celdasVisitadas);
        resultado.setOrdenVisitas(new ArrayList<>(ordenVisitas)); // NUEVO

        long tiempoNs = System.nanoTime() - tiempoInicio;
        resultado.setTiempoEjecucionNs(tiempoNs);

        return resultado;
    }

    private void buscarRecursivo(Celda actual, Celda fin) {
        if (encontrado) {
            return;
        }

        // Marcar como visitada y agregar al camino y a la animación
        actual.setVisitada(true);
        ordenVisitas.add(actual); // NUEVO
        celdasVisitadas++;
        caminoActual.add(actual);

        if (actual.equals(fin)) {
            encontrado = true;
            mejorCamino = new ArrayList<>(caminoActual);
            return;
        }

        int filaActual = actual.getFila();
        int colActual = actual.getColumna();
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