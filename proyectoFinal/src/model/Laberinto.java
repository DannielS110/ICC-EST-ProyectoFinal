package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase que representa el laberinto completo
 * VERSIÓN CORREGIDA: Garantiza que siempre hay solución
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
     * MÉTODO CORREGIDO: Genera un laberinto que siempre tiene solución
     */
    public void generarLaberintoSimple() {
        // Primero, poner todas las celdas como paredes
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].setPared(true);
                matriz[i][j].reiniciar();
            }
        }
        
        // Generar laberinto usando algoritmo recursivo
        generarLaberintoRecursivo(0, 0);
        
        // IMPORTANTE: Asegurar que inicio y fin NO sean paredes
        inicio.setPared(false);
        fin.setPared(false);
        
        // Garantizar que hay un camino al fin
        garantizarCaminoAlFin();
        
        // Crear algunos caminos adicionales para múltiples soluciones
        crearCaminosAdicionales();
    }
    
    /**
     * Garantiza que existe al menos un camino desde el inicio hasta el fin
     */
    private void garantizarCaminoAlFin() {
        // Si el fin está completamente rodeado de paredes, abrir un camino
        int filaFin = fin.getFila();
        int colFin = fin.getColumna();
        
        // Verificar si el fin está accesible
        boolean accesible = false;
        
        // Verificar vecinos del fin
        if (filaFin > 0 && !matriz[filaFin-1][colFin].esPared()) accesible = true;
        if (filaFin < filas-1 && !matriz[filaFin+1][colFin].esPared()) accesible = true;
        if (colFin > 0 && !matriz[filaFin][colFin-1].esPared()) accesible = true;
        if (colFin < columnas-1 && !matriz[filaFin][colFin+1].esPared()) accesible = true;
        
        // Si no es accesible, crear un camino
        if (!accesible) {
            // Intentar conectar con el vecino más cercano
            if (filaFin > 0) {
                matriz[filaFin-1][colFin].setPared(false);
            } else if (colFin > 0) {
                matriz[filaFin][colFin-1].setPared(false);
            } else if (filaFin < filas-1) {
                matriz[filaFin+1][colFin].setPared(false);
            } else if (colFin < columnas-1) {
                matriz[filaFin][colFin+1].setPared(false);
            }
        }
        
        // Verificar conectividad con BFS
        verificarYConectarCamino();
    }
    
    /**
     * Verifica y garantiza conectividad entre inicio y fin
     */
    private void verificarYConectarCamino() {
        // Reiniciar estados de visitado
        reiniciarVisitadas();
        
        // BFS simple para verificar conectividad
        java.util.Queue<Celda> cola = new java.util.LinkedList<>();
        cola.offer(inicio);
        inicio.setVisitada(true);
        
        boolean encontrado = false;
        
        while (!cola.isEmpty() && !encontrado) {
            Celda actual = cola.poll();
            
            if (actual.equals(fin)) {
                encontrado = true;
                break;
            }
            
            // Verificar vecinos
            int fila = actual.getFila();
            int col = actual.getColumna();
            
            int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
            for (int[] dir : dirs) {
                int nuevaFila = fila + dir[0];
                int nuevaCol = col + dir[1];
                
                if (esValida(nuevaFila, nuevaCol) && 
                    !matriz[nuevaFila][nuevaCol].esPared() && 
                    !matriz[nuevaFila][nuevaCol].isVisitada()) {
                    
                    matriz[nuevaFila][nuevaCol].setVisitada(true);
                    cola.offer(matriz[nuevaFila][nuevaCol]);
                }
            }
        }
        
        // Si no se encontró camino, crear uno directo
        if (!encontrado) {
            crearCaminoDirecto();
        }
        
        // Limpiar estados de visitado
        reiniciarVisitadas();
    }
    
    /**
     * Crea un camino directo simple entre inicio y fin
     */
    private void crearCaminoDirecto() {
        int filaActual = inicio.getFila();
        int colActual = inicio.getColumna();
        int filaFin = fin.getFila();
        int colFin = fin.getColumna();
        
        // Primero moverse horizontalmente
        while (colActual != colFin) {
            if (colActual < colFin) {
                colActual++;
            } else {
                colActual--;
            }
            if (esValida(filaActual, colActual)) {
                matriz[filaActual][colActual].setPared(false);
            }
        }
        
        // Luego moverse verticalmente
        while (filaActual != filaFin) {
            if (filaActual < filaFin) {
                filaActual++;
            } else {
                filaActual--;
            }
            if (esValida(filaActual, colActual)) {
                matriz[filaActual][colActual].setPared(false);
            }
        }
    }
    
    /**
     * Genera el laberinto usando un algoritmo recursivo con backtracking
     */
    private void generarLaberintoRecursivo(int fila, int col) {
        matriz[fila][col].setPared(false);
        
        // Direcciones: arriba, derecha, abajo, izquierda (saltando una celda)
        int[][] direcciones = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        
        // Mezclar direcciones aleatoriamente
        for (int i = direcciones.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = direcciones[i];
            direcciones[i] = direcciones[j];
            direcciones[j] = temp;
        }
        
        // Intentar cada dirección
        for (int[] dir : direcciones) {
            int nuevaFila = fila + dir[0];
            int nuevaCol = col + dir[1];
            
            if (esValida(nuevaFila, nuevaCol) && matriz[nuevaFila][nuevaCol].esPared()) {
                // Abrir el camino entre la celda actual y la nueva
                int paredFila = fila + dir[0] / 2;
                int paredCol = col + dir[1] / 2;
                matriz[paredFila][paredCol].setPared(false);
                
                // Continuar recursivamente
                generarLaberintoRecursivo(nuevaFila, nuevaCol);
            }
        }
    }
    
    /**
     * Crea caminos adicionales para hacer el laberinto más interesante
     */
    private void crearCaminosAdicionales() {
        int caminosExtras = Math.min(filas * columnas / 50, 10);
        
        for (int i = 0; i < caminosExtras; i++) {
            int fila = 1 + random.nextInt(filas - 2);
            int col = 1 + random.nextInt(columnas - 2);
            
            // Verificar que tiene al menos 2 vecinos que son camino
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
     * Genera un laberinto con un patrón específico
     */
    public void generarLaberintoPatron() {
        limpiar();
        
        // Crear un patrón de paredes
        for (int i = 1; i < filas - 1; i += 2) {
            for (int j = 1; j < columnas - 1; j++) {
                if (j % 3 != 0) {
                    matriz[i][j].setPared(true);
                }
            }
        }
        
        inicio.setPared(false);
        fin.setPared(false);
        
        garantizarCaminoAlFin();
    }
    
    /**
     * Limpia el laberinto (todas las celdas como camino)
     */
    public void limpiar() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].setPared(false);
                matriz[i][j].reiniciar();
            }
        }
    }
    
    /**
     * Reinicia solo los estados de visitado
     */
    public void reiniciarVisitadas() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].reiniciar();
            }
        }
    }
    
    /**
     * Obtiene los vecinos accesibles de una celda
     */
    public List<Celda> getVecinos(Celda celda) {
        List<Celda> vecinos = new ArrayList<>();
        int fila = celda.getFila();
        int col = celda.getColumna();
        
        // Arriba
        if (esCaminoLibre(fila - 1, col)) vecinos.add(matriz[fila - 1][col]);
        // Derecha
        if (esCaminoLibre(fila, col + 1)) vecinos.add(matriz[fila][col + 1]);
        // Abajo
        if (esCaminoLibre(fila + 1, col)) vecinos.add(matriz[fila + 1][col]);
        // Izquierda
        if (esCaminoLibre(fila, col - 1)) vecinos.add(matriz[fila][col - 1]);
        
        return vecinos;
    }
    
    /**
     * Obtiene los vecinos no visitados de una celda
     */
    public List<Celda> getVecinosNoVisitados(Celda celda) {
        List<Celda> vecinos = new ArrayList<>();
        
        for (Celda vecino : getVecinos(celda)) {
            if (!vecino.isVisitada()) {
                vecinos.add(vecino);
            }
        }
        
        return vecinos;
    }
    
    /**
     * Clona el laberinto actual
     */
    public Laberinto clonar() {
        Laberinto copia = new Laberinto(this.filas, this.columnas);
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                copia.matriz[i][j].setPared(this.matriz[i][j].esPared());
                copia.matriz[i][j].setVisitada(this.matriz[i][j].isVisitada());
                copia.matriz[i][j].setEnCamino(this.matriz[i][j].isEnCamino());
            }
        }
        
        copia.inicio = copia.matriz[inicio.getFila()][inicio.getColumna()];
        copia.fin = copia.matriz[fin.getFila()][fin.getColumna()];
        
        return copia;
    }
    
    // Getters
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public Celda getInicio() { return inicio; }
    public Celda getFin() { return fin; }
    public Celda[][] getMatriz() { return matriz; }
    
    // Setters para inicio y fin
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