package controllers;

import model.*;
import java.util.*;

/**
 * BFS - Explora en ondas concéntricas desde el inicio
 * "Como si me respiraran en círculos"
 */
public class BFS implements AlgoritmoLaberinto {
    private Laberinto laberinto;
    private Map<Celda, Celda> padres;
    private int celdasVisitadas;
    private String nombre;
    private List<Celda> ordenVisitas;

    public BFS(Laberinto laberinto) {
        this.laberinto = laberinto;
        this.padres = new HashMap<>();
        this.celdasVisitadas = 0;
        this.nombre = "BFS (Breadth-First Search)";
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
        padres.clear();
        celdasVisitadas = 0;
        ordenVisitas.clear();

        Queue<Celda> cola = new LinkedList<>();
        Celda inicio = laberinto.getInicio();
        Celda fin = laberinto.getFin();

        cola.offer(inicio);
        inicio.setVisitada(true);
        celdasVisitadas++;
        ordenVisitas.add(inicio);

        boolean encontrado = false;

        while (!cola.isEmpty() && !encontrado) {
            Celda actual = cola.poll();

            if (actual.equals(fin)) {
                encontrado = true;
                break;
            }

            // Explorar todos los vecinos (expansión en ondas)
            List<Celda> vecinos = laberinto.getVecinosNoVisitados(actual);

            for (Celda vecino : vecinos) {
                vecino.setVisitada(true);
                celdasVisitadas++;
                padres.put(vecino, actual);
                cola.offer(vecino);
                ordenVisitas.add(vecino);
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
        resultado.setOrdenVisitas(new ArrayList<>(ordenVisitas));

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