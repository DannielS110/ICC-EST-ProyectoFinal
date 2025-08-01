package data;

import model.ResultadoEjecucion;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Clase para guardar y leer resultados de algoritmos en CSV
 */
public class GuardadorDatos {
    private String archivoName;
    private int contadorID = 1;
    
    public GuardadorDatos() {
        this.archivoName = "resultados_laberinto.csv";
        // Leer el archivo existente para obtener el último ID
        List<String[]> registros = leerTodos();
        if (!registros.isEmpty()) {
            try {
                String ultimoID = registros.get(registros.size() - 1)[0];
                contadorID = Integer.parseInt(ultimoID) + 1;
            } catch (Exception e) {
                contadorID = registros.size() + 1;
            }
        }
    }
    
    /**
     * Guarda o actualiza un resultado en el archivo CSV
     * Si existe un registro con el mismo algoritmo Y dimensiones, lo actualiza
     */
    public void guardarResultado(ResultadoEjecucion resultado, int filas, int columnas) {
        List<String[]> registros = leerTodos();
        boolean actualizado = false;
        
        // Buscar si existe un registro con el mismo algoritmo Y las mismas dimensiones
        for (int i = 0; i < registros.size(); i++) {
            String[] registro = registros.get(i);
            if (registro.length >= 4 &&
                registro[1].equals(resultado.getNombreAlgoritmo()) &&
                registro[2].equals(String.valueOf(filas)) &&
                registro[3].equals(String.valueOf(columnas))) {
                
                // Mantener el ID original pero actualizar el resto
                String idOriginal = registro[0];
                registros.set(i, crearRegistro(idOriginal, resultado, filas, columnas));
                actualizado = true;
                break;
            }
        }
        
        // Si no existe, agregar nuevo registro
        if (!actualizado) {
            registros.add(crearRegistro(String.valueOf(contadorID++), resultado, filas, columnas));
        }
        
        // Escribir todos los registros
        escribirTodos(registros);
    }
    
    /**
     * Lee todos los resultados del archivo CSV
     */
    public List<String[]> leerTodos() {
        List<String[]> registros = new ArrayList<>();
        File archivo = new File(archivoName);
        
        if (!archivo.exists()) {
            return registros;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezados
                }
                
                String[] datos = linea.split(",");
                if (datos.length >= 8) { // Mínimo 8 columnas
                    registros.add(datos);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer resultados: " + e.getMessage());
        }
        
        return registros;
    }
    
    /**
     * Verifica si hay registros guardados
     */
    public boolean hayRegistros() {
        File archivo = new File(archivoName);
        if (!archivo.exists()) {
            return false;
        }
        
        List<String[]> registros = leerTodos();
        return !registros.isEmpty();
    }
    
    /**
     * Obtiene estadísticas resumidas de todos los algoritmos
     */
    public Map<String, EstadisticasAlgoritmo> obtenerEstadisticas() {
        Map<String, EstadisticasAlgoritmo> estadisticas = new HashMap<>();
        List<String[]> registros = leerTodos();
        
        for (String[] registro : registros) {
            if (registro.length >= 8) {
                String algoritmo = registro[1];
                long tiempoNs = Long.parseLong(registro[4]);
                int celdas = Integer.parseInt(registro[5]);
                int longitud = Integer.parseInt(registro[6]);
                boolean resuelto = registro[7].equals("SI");
                
                EstadisticasAlgoritmo stats = estadisticas.getOrDefault(algoritmo, 
                    new EstadisticasAlgoritmo(algoritmo));
                stats.agregarEjecucion(tiempoNs, celdas, longitud, resuelto);
                estadisticas.put(algoritmo, stats);
            }
        }
        
        return estadisticas;
    }
    
    /**
     * Elimina todos los registros
     */
    public void limpiarRegistros() {
        File archivo = new File(archivoName);
        if (archivo.exists()) {
            archivo.delete();
        }
        contadorID = 1;
    }
    
    private String[] crearRegistro(String id, ResultadoEjecucion resultado, int filas, int columnas) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        List<String> campos = new ArrayList<>();
        campos.add(id);
        campos.add(resultado.getNombreAlgoritmo());
        campos.add(String.valueOf(filas));
        campos.add(String.valueOf(columnas));
        campos.add(String.valueOf(resultado.getTiempoEjecucionNs())); // Tiempo en nanosegundos
        campos.add(String.valueOf(resultado.getCeldasVisitadas()));
        campos.add(String.valueOf(resultado.getLongitudCamino()));
        campos.add(resultado.isEncontroSolucion() ? "SI" : "NO");
        campos.add(resultado.isUsoProgramacionDinamica() ? "SI" : "NO");
        campos.add(String.valueOf(resultado.getCachesUtilizados()));
        campos.add(sdf.format(new Date()));
        
        return campos.toArray(new String[0]);
    }
    
    private void escribirTodos(List<String[]> registros) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoName))) {
            // Escribir encabezados
            pw.println("ID,Algoritmo,Filas,Columnas,Tiempo(ns),CeldasVisitadas,LongitudCamino,Resuelto,UsoPD,CachesUsados,Fecha");
            
            // Escribir datos
            for (String[] registro : registros) {
                pw.println(String.join(",", registro));
            }
        } catch (IOException e) {
            System.err.println("Error al escribir resultados: " + e.getMessage());
        }
    }
    
    /**
     * Clase interna para estadísticas agregadas
     */
    public static class EstadisticasAlgoritmo {
        private String nombre;
        private int ejecuciones = 0;
        private int exitos = 0;
        private long tiempoTotal = 0;
        private long tiempoMinimo = Long.MAX_VALUE;
        private long tiempoMaximo = 0;
        private int celdasTotal = 0;
        
        public EstadisticasAlgoritmo(String nombre) {
            this.nombre = nombre;
        }
        
        public void agregarEjecucion(long tiempo, int celdas, int longitud, boolean exito) {
            ejecuciones++;
            if (exito) exitos++;
            tiempoTotal += tiempo;
            tiempoMinimo = Math.min(tiempoMinimo, tiempo);
            tiempoMaximo = Math.max(tiempoMaximo, tiempo);
            celdasTotal += celdas;
        }
        
        public double getTiempoPromedio() {
            return ejecuciones > 0 ? (double) tiempoTotal / ejecuciones : 0;
        }
        
        public double getTasaExito() {
            return ejecuciones > 0 ? (double) exitos / ejecuciones * 100 : 0;
        }
        
        // Getters
        public String getNombre() { return nombre; }
        public int getEjecuciones() { return ejecuciones; }
        public int getExitos() { return exitos; }
        public long getTiempoMinimo() { return tiempoMinimo; }
        public long getTiempoMaximo() { return tiempoMaximo; }
        public int getCeldasPromedio() { 
            return ejecuciones > 0 ? celdasTotal / ejecuciones : 0; 
        }
    }
}