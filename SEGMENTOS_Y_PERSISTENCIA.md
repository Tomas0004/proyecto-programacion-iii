# Nuevas Características: Segmentos Laterales y Persistencia

## Segmentos Laterales

Los segmentos laterales permiten crear barreras o pasillos en el plano cartesiano que solo pueden ser horizontales o verticales (no diagonales). Los nodos que se encuentren bajo estos segmentos **no formarán parte del grafo**.

### Cómo usar:

1. En el panel de controles, desplázate hasta la sección **"Segmento Lateral"**
2. Ingresa las coordenadas:
   - **Nodo Inicial X**: Coordenada X donde comienza el segmento
   - **Nodo Inicial Y**: Coordenada Y donde comienza el segmento
   - **Nodo Final X**: Coordenada X donde termina el segmento
   - **Nodo Final Y**: Coordenada Y donde termina el segmento
   - **Ancho del Segmento**: El ancho de la barrera (cómo de "grueso" es el segmento)

3. Haz clic en **"Crear Segmento"**

**Restricciones:**
- El segmento DEBE ser horizontal (Y inicial = Y final) o vertical (X inicial = X final)
- No se permiten segmentos diagonales
- Los nodos bajo el segmento son automáticamente excluidos del grafo

**Visualización:**
- Los segmentos se muestran en color azul claro (`#00BFFF`)
- El ancho visual del segmento corresponde al valor especificado
- Se muestra el ancho del segmento como etiqueta en el canvas

### Ejemplo:
Para crear un segmento horizontal de ancho 2 desde (0,0) hasta (10,0):
- Nodo Inicial X: `0`
- Nodo Inicial Y: `0`
- Nodo Final X: `10`
- Nodo Final Y: `0`
- Ancho: `2.0`

---

## Persistencia del Plano

La persistencia permite guardar y cargar el estado completo del plano, incluyendo todas las figuras y segmentos laterales. Los datos se almacenan en formato **JSON** para fácil edición manual si es necesario.

### Cómo guardar el plano:

1. Desplázate hasta la sección **"Persistencia del Plano"** en el panel de controles
2. Ingresa un nombre para el archivo en **"Nombre del Archivo (sin .json)"** (ej: `mi_plano`)
3. Haz clic en **"Guardar Plano"**
4. Se guardará un archivo llamado `mi_plano.json` en el directorio actual

### Cómo cargar un plano guardado:

1. Desplázate hasta la sección **"Persistencia del Plano"**
2. Ingresa el nombre del archivo que deseas cargar (sin la extensión `.json`)
3. Haz clic en **"Cargar Plano"**
4. El plano se recargará completamente con todas las figuras y segmentos

### Estructura del archivo JSON guardado:

```json
{
  "range": [-50.0, 50.0, -50.0, 50.0],
  "figures": [
    {
      "shape": "robot",
      "x": 0.0,
      "y": 0.0,
      "size": 1.0
    },
    {
      "shape": "destination",
      "x": 20.0,
      "y": 20.0,
      "size": 1.0
    }
  ],
  "segments": [
    {
      "startX": 5.0,
      "startY": 5.0,
      "endX": 15.0,
      "endY": 5.0,
      "width": 2.0,
      "type": "HORIZONTAL"
    }
  ]
}
```

### Notas importantes:

- El archivo JSON se guarda en el **directorio actual** (donde se ejecuta la aplicación)
- Los puntos de datos son incluidos en el plano (rango, figuras, segmentos)
- Cuando cargas un plano, se **reemplaza completamente** el plano actual
- Si intenta cargar un archivo que no existe, se mostrará un mensaje de error

---

## Clases nuevas creadas:

### `LateralSegment.java`
Representa un segmento lateral con validación de orientación (horizontal/vertical).

**Métodos principales:**
- `addLateralSegment(Node start, Node end, double width)` - Añade un segmento
- `containsPoint(double x, double y)` - Verifica si un punto está bajo el segmento
- `getType()` - Retorna si es HORIZONTAL o VERTICAL

### `PlaneSerializer.java`
Maneja la serialización y deserialización del plano a/desde JSON.

**Métodos principales:**
- `savePlane()` - Guarda el plano completo en un archivo JSON
- `loadPlane()` - Carga un plano desde un archivo JSON
- `shapeFromId()` - Convierte el ID de forma a enum Shape

---

## Flujo de trabajo recomendado:

1. Crea tu plano con figuras (robot, destino, obstáculos)
2. Añade segmentos laterales para crear pasillos o barreras
3. Verifica que el grafo se genere correctamente (sin nodos bajo los segmentos)
4. Encuentra el camino más corto con Dijkstra
5. **Guarda** el plano para usarlo después
6. **Carga** el plano cuando necesites trabajar con él nuevamente

---

## Notas técnicas:

- Los segmentos se dibujan **después** de las figuras pero **antes** de las aristas, para que sean visibles pero no interfieran con el sistema de visualización
- Cada segmento utiliza reflexión Java para acceder a los campos públicos de las figuras durante la serialización
- La validación de orientación ocurre tanto al crear el segmento como durante la deserialización
