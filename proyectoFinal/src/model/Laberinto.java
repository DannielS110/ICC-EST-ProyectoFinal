package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Clase que representa el laberinto usando una matriz de Celdas
 * Versión mejorada con generación de laberintos más realistas
 */
public class Laberinto {
    private Celda[][] matriz;
    private int filas;
    private int columnas;
    private Celda inicio;
    private Celda fin;
    private Random random = new Random();
    
    public Laberinto(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.matriz = new Celda[filas][columnas];
        
        // Inicializar todas las celdas
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j] = new Celda(i, j);
            }
        }
        
        // Establecer inicio y fin por defecto
        this.inicio = matriz[0][0];
        this.fin = matriz[filas - 1][columnas - 1];
    }
    
    public Celda getCelda(int fila, int columna) {
        if (esValida(fila, columna)) {
            return matriz[fila][columna];
        }
        return null;
    }
    
    public boolean esValida(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }
    
    public boolean esCaminoLibre(int fila, int columna) {
        return esValida(fila, columna) && !matriz[fila][columna].esPared();
    }
    
    public void alternarCelda(int fila, int columna) {
        if (esValida(fila, columna)) {
            Celda celda = matriz[fila][columna];
            if (celda != inicio && celda != fin) {
                celda.alternar();
            }
        }
    }
    
    /**
     * Genera un laberinto más realista usando el algoritmo de generación recursiva
     */
    public void generarLaberintoSimple() {
        // Primero, hacer todas las celdas paredes
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].setPared(true);
                matriz[i][j].reiniciar();
            }
        }
        
        // Generar laberinto usando algoritmo de backtracking recursivo
        generarLaberintoRecursivo(0, 0);
        
        // Asegurar que inicio y fin sean caminos
        inicio.setPared(false);
        fin.setPared(false);
        
        // Crear algunos caminos adicionales para múltiples soluciones
        crearCaminosAdicionales();
    }
    
    /**
     * Algoritmo de generación recursiva de laberintos
     */
    private void generarLaberintoRecursivo(int fila, int col) {
        // Marcar la celda actual como camino
        matriz[fila][col].setPared(false);
        
        // Crear lista de direcciones y mezclarlas aleatoriamente
        int[][] direcciones = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        for (int i = direcciones.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = direcciones[i];
            direcciones[i] = direcciones[j];
            direcciones[j] = temp;
        }
        
        // Explorar en cada dirección
        for (int[] dir : direcciones) {
            int nuevaFila = fila + dir[0];
            int nuevaCol = col + dir[1];
            
            // Verificar si la nueva posición es válida y es una pared
            if (esValida(nuevaFila, nuevaCol) && matriz[nuevaFila][nuevaCol].esPared()) {
                // Crear camino entre la celda actual y la nueva celda
                int paredFila = fila + dir[0] / 2;
                int paredCol = col + dir[1] / 2;
                matriz[paredFila][paredCol].setPared(false);
                
                // Continuar recursivamente desde la nueva posición
                generarLaberintoRecursivo(nuevaFila, nuevaCol);
            }
        }
    }
    
    /**
     * Crea algunos caminos adicionales para hacer el laberinto más interesante
     */
    private void crearCaminosAdicionales() {
        int caminosExtras = Math.min(filas * columnas / 50, 10);
        
        for (int i = 0; i < caminosExtras; i++) {
            int fila = 1 + random.nextInt(filas - 2);
            int col = 1 + random.nextInt(columnas - 2);
            
            // Solo convertir a camino si tiene al menos 2 vecinos que son caminos
            int vecinosCamino = 0;
            if (fila > 0 && !matriz[fila-1][col].esPared()) vecinosCamino++;
            if (fila < filas-1 && !matriz[fila+1][col].esPared()) vecinosCamino++;
            if (col > 0 && !matriz[fila][col-1].esPared()) vecinosCamino++;
            if (col < columnas-1 && !matriz[fila][col+1].esPared()) vecinosCamino++;
            
            if (vecinosCamino >= 2) {
                matriz[fila][col].setPared(false);
            }
        }
    }
    
    /**
     * Genera un laberinto con patrón simple (método anterior como alternativa)
     */
    public void generarLaberintoPatron() {
        limpiar();
        
        // Crear patron fijo de paredes
        for (int i = 1; i < filas - 1; i += 2) {
            for (int j = 1; j < columnas - 1; j++) {
                if (j % 3 != 0) {
                    matriz[i][j].setPared(true);
                }
            }
        }
        
        inicio.setPared(false);
        fin.setPared(false);
    }
    
    public void limpiar() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].setPared(false);
                matriz[i][j].reiniciar();
            }
        }
    }
    
    public void reiniciarVisitadas() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].reiniciar();
            }
        }
    }
    
    public List<Celda> getVecinos(Celda celda) {
        List<Celda> vecinos = new ArrayList<>();
        int fila = celda.getFila();
        int col = celda.getColumna();
        
        if (esCaminoLibre(fila - 1, col)) vecinos.add(matriz[fila - 1][col]);
        if (esCaminoLibre(fila, col + 1)) vecinos.add(matriz[fila][col + 1]);
        if (esCaminoLibre(fila + 1, col)) vecinos.add(matriz[fila + 1][col]);
        if (esCaminoLibre(fila, col - 1)) vecinos.add(matriz[fila][col - 1]);
        
        return vecinos;
    }
    
    public List<Celda> getVecinosNoVisitados(Celda celda) {
        List<Celda> vecinos = new ArrayList<>();
        
        for (Celda vecino : getVecinos(celda)) {
            if (!vecino.isVisitada()) {
                vecinos.add(vecino);
            }
        }
        
        return vecinos;
    }
    
    public Laberinto clonar() {
        Laberinto copia = new Laberinto(this.filas, this.columnas);
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                copia.matriz[i][j].setPared(this.matriz[i][j].esPared());
            }
        }
        
        copia.inicio = copia.matriz[inicio.getFila()][inicio.getColumna()];
        copia.fin = copia.matriz[fin.getFila()][fin.getColumna()];
        
        return copia;
    }
    
    // Getters y Setters
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public Celda getInicio() { return inicio; }
    public Celda getFin() { return fin; }
    public Celda[][] getMatriz() { return matriz; }
    
    public void setInicio(int fila, int columna) {
        if (esValida(fila, columna) && !matriz[fila][columna].esPared()) {
            this.inicio = matriz[fila][columna];
        }
    }
    
    public void setFin(int fila, int columna) {
        if (esValida(fila, columna) && !matriz[fila][columna].esPared()) {
            this.fin = matriz[fila][columna];
        }
    }
}