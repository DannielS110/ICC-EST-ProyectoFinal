package controllers;

import model.ResultadoEjecucion;

/**
 * Interfaz base para todos los algoritmos de resolución de laberintos
 * Define el contrato que deben cumplir todos los algoritmos
 */
public interface AlgoritmoLaberinto {
    
    /**
     * Resuelve el laberinto y retorna los resultados de la ejecución
     * @return ResultadoEjecucion con las métricas y el camino encontrado
     */
    ResultadoEjecucion resolver();
    
    /**
     * Obtiene el nombre descriptivo del algoritmo
     * @return String con el nombre del algoritmo
     */
    String getNombre();
}