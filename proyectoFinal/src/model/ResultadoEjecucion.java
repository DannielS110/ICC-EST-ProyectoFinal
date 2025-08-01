package model;

import java.util.List;
import java.util.ArrayList;

/**
 * Clase para almacenar los resultados de la ejecución de un algoritmo
 * Incluye métricas de Programación Dinámica
 */
public class ResultadoEjecucion {
    private String nombreAlgoritmo;
    private long tiempoEjecucion; // En milisegundos
    private long tiempoEjecucionNs; // En nanosegundos
    private int celdasVisitadas;
    private List<Celda> camino;
    private boolean encontroSolucion;
    private List<Celda> ordenVisitas;
    
    // Campos para Programación Dinámica
    private boolean usoProgramacionDinamica;
    private int cachesUtilizados;
    private int tamanoCache;
    
    public ResultadoEjecucion(String nombreAlgoritmo) {
        this.nombreAlgoritmo = nombreAlgoritmo;
        this.camino = new ArrayList<>();
        this.celdasVisitadas = 0;
        this.encontroSolucion = false;
        this.usoProgramacionDinamica = false;
        this.cachesUtilizados = 0;
        this.tamanoCache = 0;
        this.ordenVisitas = new ArrayList<>();
    }
    
    // Getters y Setters existentes
    public String getNombreAlgoritmo() { 
        return nombreAlgoritmo; 
    }
    
    public long getTiempoEjecucion() { 
        return tiempoEjecucion; 
    }
    
    public void setTiempoEjecucion(long tiempoEjecucion) { 
        this.tiempoEjecucion = tiempoEjecucion; 
    }
    
    public long getTiempoEjecucionNs() { 
        return tiempoEjecucionNs; 
    }
    
    public void setTiempoEjecucionNs(long tiempoEjecucionNs) { 
        this.tiempoEjecucionNs = tiempoEjecucionNs;
        // Actualizar también el tiempo en milisegundos
        this.tiempoEjecucion = tiempoEjecucionNs / 1_000_000;
    }
    
    public int getCeldasVisitadas() { 
        return celdasVisitadas; 
    }
    
    public void setCeldasVisitadas(int celdasVisitadas) { 
        this.celdasVisitadas = celdasVisitadas; 
    }
    
    public List<Celda> getCamino() { 
        return camino; 
    }
    
    public void setCamino(List<Celda> camino) { 
        this.camino = camino; 
    }
    
    public boolean isEncontroSolucion() { 
        return encontroSolucion; 
    }
    
    public void setEncontroSolucion(boolean encontroSolucion) { 
        this.encontroSolucion = encontroSolucion; 
    }
    
    public int getLongitudCamino() { 
        return camino.size(); 
    }
    
    // Nuevos métodos para Programación Dinámica
    public boolean isUsoProgramacionDinamica() { 
        return usoProgramacionDinamica; 
    }
    
    public void setUsoProgramacionDinamica(boolean usoProgramacionDinamica) { 
        this.usoProgramacionDinamica = usoProgramacionDinamica; 
    }
    
    public int getCachesUtilizados() { 
        return cachesUtilizados; 
    }
    
    public void setCachesUtilizados(int cachesUtilizados) { 
        this.cachesUtilizados = cachesUtilizados; 
    }
    
    public void incrementarCachesUtilizados() {
        this.cachesUtilizados++;
    }
    
    public int getTamanoCache() { 
        return tamanoCache; 
    }
    
    public void setTamanoCache(int tamanoCache) { 
        this.tamanoCache = tamanoCache; 
    }
    
    public void setOrdenVisitas(List<Celda> ordenVisitas) { this.ordenVisitas = ordenVisitas; }
    
    public List<Celda> getOrdenVisitas() { return ordenVisitas; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResultadoEjecucion{");
        sb.append("algoritmo='").append(nombreAlgoritmo).append('\'');
        sb.append(", tiempoMs=").append(tiempoEjecucion);
        sb.append(", tiempoNs=").append(tiempoEjecucionNs);
        sb.append(", celdasVisitadas=").append(celdasVisitadas);
        sb.append(", longitudCamino=").append(getLongitudCamino());
        sb.append(", encontroSolucion=").append(encontroSolucion);
        if (usoProgramacionDinamica) {
            sb.append(", usoPD=true");
            sb.append(", cachesUsados=").append(cachesUtilizados);
            sb.append(", tamanoCache=").append(tamanoCache);
        }
        sb.append('}');
        return sb.toString();
    }
}