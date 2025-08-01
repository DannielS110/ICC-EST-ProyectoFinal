package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j] = new Celda(i, j);
            }
        }
        
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
    
    
    public void generarLaberintoSimple() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j].setPared(true);
                matriz[i][j].reiniciar();
            }
        }
        
        generarLaberintoRecursivo(0, 0);
                inicio.setPared(false);
        fin.setPared(false);
        
        crearCaminosAdicionales();
    }
    

    private void generarLaberintoRecursivo(int fila, int col) {
        matriz[fila][col].setPared(false);
        
        int[][] direcciones = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        for (int i = direcciones.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = direcciones[i];
            direcciones[i] = direcciones[j];
            direcciones[j] = temp;
        }
        
        for (int[] dir : direcciones) {
            int nuevaFila = fila + dir[0];
            int nuevaCol = col + dir[1];
            
            if (esValida(nuevaFila, nuevaCol) && matriz[nuevaFila][nuevaCol].esPared()) {                int paredFila = fila + dir[0] / 2;
                int paredCol = col + dir[1] / 2;
                matriz[paredFila][paredCol].setPared(false);
                
                generarLaberintoRecursivo(nuevaFila, nuevaCol);
            }
        }
    }
    
   
    private void crearCaminosAdicionales() {
        int caminosExtras = Math.min(filas * columnas / 50, 10);
        
        for (int i = 0; i < caminosExtras; i++) {
            int fila = 1 + random.nextInt(filas - 2);
            int col = 1 + random.nextInt(columnas - 2);
            
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
    

    public void generarLaberintoPatron() {
        limpiar();
        
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
        for (int i = 0; i < filas; i++) {for (int j = 0; j < columnas; j++) {
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
    }    public int getFilas() { return filas; }
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