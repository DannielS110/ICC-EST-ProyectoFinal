package controllers;

import model.*;
import java.util.*;

/**
 * DFS - Se lanza al abismo sin mirar atrás
 * "Como una aguja que perfora"
 */
public class DFS implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private int celdasVisitadas;
    private String nombre;
    private List<Celda> ordenVisitas;
    private List<Celda> caminoFinal;
    private boolean encontrado;

    public DFS(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.celdasVisitadas = 0;
        this.nombre = "DFS (Depth-First Search)";
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

        laberinto.reiniciarVisitadas();
        celdasVisitadas = 0;
        ordenVisitas.clear();
        caminoFinal = null;
        encontrado = false;

        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();

        // Iniciar búsqueda profunda
        List<Celda> caminoActual = new ArrayList<>();
        buscarDFS(inicio, fin, caminoActual);

        if (caminoFinal != null) {
            resultado.setCamino(caminoFinal);
            resultado.setEncontroSolucion(true);

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

    private void buscarDFS(Celda actual, Celda fin, List<Celda> caminoActual) {
        if (encontrado) return;

        // Marcar como visitada (cicatriz gris)
        actual.setVisitada(true);
        celdasVisitadas++;
        caminoActual.add(actual);
        ordenVisitas.add(actual);

        // Si llegamos al fin
        if (actual.equals(fin)) {
            encontrado = true;
            caminoFinal = new ArrayList<>(caminoActual);
            return;
        }

        // Explorar vecinos profundamente
        List<Celda> vecinos = laberinto.getVecinosNoVisitados(actual);
        for (Celda vecino : vecinos) {
            buscarDFS(vecino, fin, caminoActual);
            if (encontrado) return;
        }

        // Backtrack (pero la cicatriz gris permanece)
        caminoActual.remove(caminoActual.size() - 1);
    }
}