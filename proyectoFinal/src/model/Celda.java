package model;

/**
 * Representa una celda individual del laberinto
 * VERSIÓN CORREGIDA: Con equals y hashCode correctos
 */
public class Celda {
    private int fila;
    private int columna;
    private boolean esPared;
    private boolean visitada;
    private boolean enCamino;
    
    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.esPared = false;
        this.visitada = false;
        this.enCamino = false;
    }
    
    // Getters y Setters
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public boolean esPared() { return esPared; }
    public void setPared(boolean esPared) { this.esPared = esPared; }
    public boolean isVisitada() { return visitada; }
    public void setVisitada(boolean visitada) { this.visitada = visitada; }
    public boolean isEnCamino() { return enCamino; }
    public void setEnCamino(boolean enCamino) { this.enCamino = enCamino; }
    
    public void alternar() {
        this.esPared = !this.esPared;
    }
    
    public void reiniciar() {
        this.visitada = false;
        this.enCamino = false;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Celda otra = (Celda) obj;
        return this.fila == otra.fila && this.columna == otra.columna;
    }
    
    @Override
    public int hashCode() {
        return 31 * fila + columna;
    }
    
    @Override
    public String toString() {
        return "Celda(" + fila + ", " + columna + ")";
    }
}