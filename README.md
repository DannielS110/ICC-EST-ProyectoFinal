![alt text](image.png)
---
# Resolvedor de Laberintos - Proyecto Final

## 🎮 Descripción del problema.
El proyecto consiste en implementar diferentes algoritmos para encontrar la ruta óptima 
en un laberinto desde un punto inicial (A) hasta un destino (B), utilizando estructuras 
de datos avanzadas, programación dinámica y patrones de diseño como el MVC. 
El laberinto se representa mediante una matriz donde cada celda puede ser transitable o no.

## Propuesta de Solución

### Marco Teórico
- **DFS (Depth-First Search):** búsqueda en profundidad.
- **BFS (Breadth-First Search):** búsqueda en amplitud.
- **Backtracking:** técnica recursiva para explorar rutas alternativas y retroceder al encontrar caminos sin salida.
- **Programación Dinámica:** incluye técnicas como memoización y tabulación para optimizar búsquedas repetitivas.

### Tecnologías Utilizadas
- Lenguaje: Java
- Interfaz Gráfica: Swing
- Patrón de diseño: MVC (Modelo-Vista-Controlador)
- Almacenamiento de datos: CSV para tiempos de ejecución

---
## 📰 Diagrama UML
### - Diagrama UML Proyecto
![alt text](image-1.png)


---
## 🗃️ Clases DAO (Data Access Object)
Las clases DAO en el proyecto son responsables de la persistencia de datos: se encargan de guardar y recuperar la información generada por los algoritmos y el usuario.
Esto incluye el almacenamiento de laberintos, tiempos de ejecución y resultados históricos en archivos (CSV).

Ventajas del patrón DAO:
Separación de responsabilidades: La lógica de negocio no depende directamente de la gestión de archivos.

Facilita mantenimiento: Si cambias el formato de almacenamiento, no tienes que tocar el resto del código.

Reusabilidad: Los métodos DAO pueden ser usados por cualquier parte del programa.

---
### Ejemplo en el proyecto:
GuardadorDatos.java
Esta clase contiene métodos para guardar resultados de cada ejecución de los algoritmos (tiempo, celdas visitadas, longitud del camino, algoritmo usado) en archivos CSV.

Métodos típicos:

guardarResultado(ResultadoEjecucion resultado): Registra los datos de la resolución en el CSV.

leerResultados(): Lee y retorna todos los resultados almacenados para mostrar en tablas o gráficas.

Uso real: Cada vez que ejecutas un algoritmo y termina, el resultado se almacena automáticamente. Luego puedes visualizar el historial en la sección de estadísticas y comparar el desempeño de los algoritmos.

### 🧩 Clases Laberinto (Maze)
Las clases relacionadas con el laberinto son el corazón de la lógica del proyecto.
Aquí se representa, manipula y consulta toda la información de la estructura del laberinto.

Principales clases y funciones:
Laberinto.java

Representa el laberinto como una matriz de celdas (objetos de la clase Celda).

#### Permite:

Consultar o modificar si una celda es transitable (camino) o no (pared).

Cambiar las posiciones de inicio (A) y fin (B).

Proveer los vecinos accesibles de una celda para los algoritmos de búsqueda.

Generar laberintos automáticos o reiniciar estados (visitadas/camino).

Es el modelo que usan los algoritmos para resolver el laberinto.

Celda.java

Define el estado de cada posición de la matriz: si es camino, pared, visitada, parte del camino final, etc.

ResultadoEjecucion.java

Encapsula los resultados de cada algoritmo: camino encontrado, tiempo, celdas visitadas, orden de visita, etc.
- **DFSMazeSolver.java**: Implementación del algoritmo DFS.
- **BFSMazeSolver.java**: Implementación del algoritmo BFS.
- **BacktrackingMazeSolver.java**: Implementación del algoritmo Backtracking.

### Interfaz
Se implementó una interfaz gráfica simple utilizando Swing, permitiendo al usuario:
- Configurar dinámicamente el tamaño del laberinto.
- Definir celdas transitables o bloqueadas.
- Seleccionar los puntos inicial y final.
- Visualizar gráficamente la ruta óptima encontrada.

---

## 🖼️ Ejemplo de Funcionamiento
![alt text](image-2.png)
![alt text](image-3.png)
![alt text](image-4.png)
![alt text](image-6.png)
![alt text](image-5.png)
---
## ♨️ Código Ejemplo
```java
public class DFS implements public class BFS implements AlgoritmoLaberinto {
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
```

---
## 🚀 Ejecutar la Aplicación

### Opción 1: JAR Ejecutable (Recomendado)
```bash
java -jar App.jar
```


## 📁 Estructura del Proyecto

```
PROYECTOFINAL/
├── lib/            
│       ├────────   # Bibliotecas externas
├── src/
│   ├── controller/ 
│       ├────────   # AlgoritmoLaberinto
│       ├────────   # BFS
│       ├────────   # DFS
│       ├────────   # RecursivoBacktraking
│       ├────────   # RecursivoCuatroDirecciones
│       ├────────   # RecursivoDosDirecciones
│   ├── dao/        
│       ├────────   # GuardarDatos
│   ├── images/     
│       ├────────   # Recursos gráficos
│   ├── model/      
│       ├────────   # Celda
│       ├────────   # Laberinto
│       ├────────   # ResultadoEjecución
│   ├── view/       
│       ├────────   # Panellaberinto
│       ├────────   # PantallaEquipo
│       ├────────   # PantallaInicio
│       ├────────   # VentanaComparación
│       ├────────   # VentanaEstadisticas
│       ├────────   # VentanaGameplay
│       ├────────   # VentanaGraficos
├── App.java
├── compilar.bad
├── ejecutar.bat 
└── resultados_laberinto.cvs    
```



## 🎯 Características

### Algoritmos Implementados
- **BFS (Breadth-First Search)**
- **DFS (Depth-First Search)**
- **Recursivo (2 direcciones)**
- **Recursivo (4 direcciones)**
- **Recursivo (4 direcciones + Backtracking)**

### Funcionalidades
- ✅ Generación automática de laberintos
- ✅ Edición manual de paredes
- ✅ Visualización en tiempo real
- ✅ Estadísticas de rendimiento
- ✅ Persistencia de resultados
- ✅ Gráficos de comparación
- ✅ Tema visual Minecraft

## 🎨 Interfaz Visual

### Flujo de Navegación
1. **SplashScreen** - Pantalla de carga (5 segundos)
2. **IntermediatePanel** - Panel con imagen de integrantes
3. **Pantalla Inicio** - Aplicación principal

### Temática Minecraft
- Colores de bloques de Minecraft
- Fuente monospaciada
- Estilo visual consistente
- Imágenes de fondo temáticas

## 📊 Estadísticas y Resultados

### Persistencia de Datos
- Archivo CSV: `resultados_laberintos.csv`
- Cada ejecución es un registro separado
- Métricas: tiempo, celdas visitadas, longitud del camino

### Visualización
- Tabla de resultados históricos
- Gráficos de barras y pastel
- Comparación entre algoritmos

## 🛠️ Requisitos del Sistema

- **Java 8 o superior**
- **Memoria RAM:** Mínimo 512MB
- **Sistema Operativo:** Windows, macOS, Linux

## 📦 Crear JAR Ejecutable

### Usando PowerShell
```powershell
.\build.ps1
```

### Usando Batch (Windows)
```cmd
build.bat
```

### Manualmente
```bash
# Compilar
javac -d build -cp "src;lib/*" src/**/*.java

# Crear JAR
jar cfm MazeSolver.jar MANIFEST.MF -C build .
```

## 🎮 Cómo Usar

1. **Ejecutar la aplicación**
2. **Generar laberinto** - Hacer clic en "Generar"
3. **Establecer puntos** - Hacer clic para marcar inicio y fin
4. **Seleccionar algoritmo** - Elegir método de resolución
5. **Resolver** - Hacer clic en "Resolver"
6. **Ver estadísticas** - Hacer clic en "Mostrar Estadísticas"
7. **Editar paredes** - Hacer clic en "Editar Paredes"

## 📈 Algoritmos y Rendimiento

### BFS (Breadth-First Search)
- **Ventaja:** Encuentra el camino más corto
- **Desventaja:** Puede visitar muchas celdas

### DFS (Depth-First Search)
- **Ventaja:** Rápido en laberintos simples
- **Desventaja:** No garantiza el camino más corto

### Recursivo (2 direcciones)
- **Ventaja:** Simple y eficiente
- **Desventaja:** Limitado a 2 direcciones

### Recursivo (4 direcciones)
- **Ventaja:** Más flexible
- **Desventaja:** Puede ser más lento

### Recursivo (4 direcciones + Backtracking)
- **Ventaja:** Mejor rendimiento
- **Desventaja:** Más complejo

---

## 🪪 Conclusiones

### - Daniel Sanches:

El uso de clases Data para manejar datos persistentes en archivos CSV 
fue una decisión acertada, ya que permitió medir y comparar de forma 
objetiva los tiempos de ejecución de cada algoritmo. Esta funcionalidad
brindó un valor agregado al sistema al permitir una evaluación cuantitativa de la eficiencia algorítmica, fomentando una visión analítica en el desarrollo de software.
### - Daniel Durán:
El proyecto de resolución de laberintos nos permitió aplicar 
conocimientos previos en Programación y Estructura de Datos 
implementando y compararon múltiples algoritmos de búsqueda 
(DFS, BFS, recursivo con 2 y 4 direcciones, backtracking), con esto logrando una resolucion de laberintos mas eficientes y con menos tiempo de carga.

### - Joey Diaz:
Este proyecto me ayudo a sentrarme en la logica de los algoritmos, el manejo de la programacion dinamica, guardando datos de celdas visitadas en un array list, siendo esto lo principal de la unidad final y formando parte de la base del proyecto, este proyecto logro en mi reforzar la parte de interfaz grafica tema que no se a podido profundisar en clases de POO con el Ing. Juan Pablo Bermeo.

### - Nelson Villalta:
En este proyecto se a logrado una avanzada comprensión y un evidente estilo de aprendizaje que logro combinar diferentes tipos de métodos como el DFS, FBS y uno de los más importantes el cual es backtraking recursivo. Además de usar java swing y java awt, para poder crear una interfaz grafica intuitiva retro y sobre todo interactiva.


*¡Disfruta resolviendo laberintos! * 🎮

© Sanchez / Durán / Diaz / Villalta | 2025
