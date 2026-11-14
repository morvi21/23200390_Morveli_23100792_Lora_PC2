# Diario espacial – Práctica Calificada 2 (DAM 2026-2)

Aplicación Android en **Kotlin + Jetpack Compose** que consume la API abierta de la NASA
(<https://api.nasa.gov>) y guarda los datos en SQLite mediante **Room**.

## Integrantes

| Rol | Código | Integrante | Opción |
|---|---|---|---|
| Alumno 1 | 23200390 | Morveli | Imágenes (APOD) |
| Alumno 2 | 23100792 | Jean Paul Lora Sambrano | Asteroides (NeoWs) |

## Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo en Android Studio.
2. Generar una clave en <https://api.nasa.gov> (sección *Generate API Key*).
3. Abrir el archivo **`local.properties`** de la raíz del proyecto (Android Studio lo crea
   automáticamente; **no se sube a GitHub** porque está en `.gitignore`) y agregar:

   ```properties
   NASA_API_KEY=tu_clave_aqui
   ```

4. Hacer *Sync Project with Gradle Files* y ejecutar la configuración `app` en un emulador
   o dispositivo con internet.

La clave se lee en `app/build.gradle.kts` y se expone al código como
`BuildConfig.NASA_API_KEY`. Nunca aparece en ningún commit.

## Arquitectura

```
data/
  remote/
    RetrofitClient.kt        (común)  Retrofit + Gson, URL base https://api.nasa.gov/
    ApodApi.kt               (A1)     GET science.nasa.gov/wp-json/wp/v2/apod-basic (ver nota)
    dto/ApodDto.kt           (A1)     DTO + conversión explícita DTO -> Entity
    NeoApi.kt                (A2)     GET neo/rest/v1/feed
    dto/NeoFeedDto.kt        (A2)     DTOs (todos los campos anulables)
    dto/NeoMapper.kt         (A2)     conversión explícita DTO -> Entity
  local/
    AppDatabase.kt           (común)  clase Database de Room
    ApodEntity.kt / ApodDao.kt         (A1)  tabla "apods"
    AsteroidEntity.kt / AsteroidDao.kt (A2)  tabla "asteroides"
  repository/
    ApodRepository.kt        (A1)     API -> Room; la UI solo lee de Room
    AsteroidRepository.kt    (A2)     API -> Room; la UI solo lee de Room
AppContainer.kt / DiarioEspacialApp.kt (común)  creación de repositorios
ui/
  navigation/AppNavigation.kt (común) barra inferior Imágenes | Asteroides
  imagenes/                   (A1)    ViewModels (StateFlow), lista, detalle, favoritas
  asteroides/                 (A2)    ViewModels (StateFlow), lista, detalle, favoritos
```

Flujo de datos: **API → DTO → mapper → Entity → Room → Flow → ViewModel (StateFlow) → pantalla**.

### Opción Imágenes (Alumno 1)

- Lista de APOD de los últimos 7 días (hoy − 6 días hasta hoy), de la más reciente a la más
  antigua, con imagen, título y fecha. Si `media_type` es `video` se muestra
  “Contenido en video” en lugar de la imagen.
- Detalle con la imagen en grande (`hdurl` o, si no existe, `url`), título, fecha,
  explicación completa y autor (`copyright`) cuando existe.
- Favoritos (★/☆) en la lista y el detalle; pantalla **“Mis imágenes favoritas”** desde la
  barra superior.

### Opción Asteroides (Alumno 2)

- Lista de los asteroides de los últimos 7 días (hoy − 6 días hasta hoy), en una sola lista
  ordenada por fecha de aproximación (más recientes primero).
- Cada elemento: nombre, fecha, diámetro máximo (m), velocidad (km/h) y distancia (km),
  con máximo dos decimales. Los potencialmente peligrosos tienen fondo rojo y etiqueta ⚠.
- Interruptor **“Solo potencialmente peligrosos”**.
- Detalle con nombre, id, fecha, diámetro mínimo y máximo, velocidad, distancia y peligrosidad.
- Favoritos (★/☆) en la lista y el detalle; pantalla **“Mis asteroides”** desde la barra superior.
- Indicador de carga, mensaje cuando no hay datos y manejo de campos nulos o ausentes
  (ver `NeoMapperTest`).

## Acuerdo de coordinación (base común)

- **Quién prepara la base común:** el Alumno 2 (Jean Paul Lora) la preparó en la rama
  `feature/asteroides`: `RetrofitClient`, `AppDatabase`, `AppContainer`/`DiarioEspacialApp`
  y la navegación principal (`AppNavigation`), además de las dependencias en Gradle.
- **Cómo la integra el Alumno 1:** después de que se fusione el PR del Alumno 2, el Alumno 1
  actualiza su rama `feature/imagenes` desde `main` y:
  1. Crea su `ApodApi` con `RetrofitClient.create<ApodApi>()`.
  2. Agrega `ApodEntity::class` en `AppDatabase.entities`, sube `version` a 2 y agrega
     `abstract fun apodDao(): ApodDao`.
  3. Agrega su `ApodRepository` en `AppContainer`.
  4. Reemplaza el marcador de `ui/imagenes/ImagenesNavGraph.kt` por sus pantallas,
     manteniendo la ruta `ImagenesRoutes.GRAPH = "imagenes"`.
  5. Agrega la dependencia de Coil si la necesita para las imágenes.
- No se cambian los nombres de las rutas de los grafos ni el nombre de la base de datos sin
  avisar al compañero.

### Nota sobre el servicio APOD

En noviembre de 2026 `https://api.nasa.gov/planetary/apod` responde para todas las fechas
con el mismo marcador (título "NASA Science" y el logo de la NASA), porque NASA migró APOD
a science.nasa.gov. Por eso la opción Imágenes usa el servicio nuevo **APOD Basic**
(`https://science.nasa.gov/wp-json/wp/v2/apod-basic?date_from=AAMMDD&date_to=AAMMDD`), que
devuelve los mismos campos (`date`, `title`, `explanation`, `url`, `hdurl`, `media_type`,
`copyright`). En ese servicio `url` es la página del artículo y la imagen está en `hdurl`.

### Integración realizada

La primera versión de la opción Imágenes se entregó en el PR #2 dentro de un paquete aparte
(`pe.edu.demo.diarioespacial`) con su propio `MainActivity` y su propia base de datos.
En la rama `feature/integracion-apod` se movió a la base común siguiendo los pasos de arriba
(una sola `AppDatabase` versión 2, un solo `RetrofitClient` y una sola barra inferior) y se
eliminó el código duplicado.

## Flujo de trabajo en Git

- `main` está protegida: no se hacen commits directos.
- Cada integrante trabaja en su rama (`feature/asteroides`, `feature/imagenes`) y entrega su
  parte con un Pull Request que revisa y aprueba el otro integrante, con al menos un comentario.
