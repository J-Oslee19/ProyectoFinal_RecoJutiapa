#*Video de la Fase 1*
https://drive.google.com/file/d/1zFTT0BSH5ARVjVtD-0oZxg8HazFoHqdT/view?usp=drivesdk

#*Video de Flujo de n8n de Expocicion de la app Actualizada*
*DESCRIPCION DEL PROYECTO*
*INSTRUCCIONES DE INSTALACIÓN*
*EXPLICACIÓN DE LA INTEGRACIÓN CON N8N*
*CAPTURAS DE PANTALLA O DIFs DEMOSTRATIVOS*
*REQUISITOS Y DEPENDENCIAS*

*FLUJO DE n8n(archivo .json)*

*DOCUMENTO DE PROPUESTA*

# RecoJutiapa

RecoJutiapa es una app Android para recomendaciones personalizadas de productos y servicios locales para estudiantes de Jutiapa/UMG.

## Requisitos
- Android Studio Flamingo o superior
- JDK 17+
- minSdk 24, targetSdk actual
- Clave API de Gemini (Google Generative Language API)

## Instalación

1. Clona el repositorio.
2. Crea un archivo `local.properties` en la raíz del proyecto y agrega:
   ```
   GEMINI_API_KEY=tu_clave_gemini
   ```
3. Abre el proyecto en Android Studio.
4. Sincroniza Gradle y ejecuta en un emulador/dispositivo.

## Permisos requeridos
- INTERNET (obligatorio)
- ACCESS_FINE_LOCATION (opcional, para recomendaciones basadas en ubicación)

## Datos de ejemplo
- El sistema carga datos de negocios desde `res/raw/negocios_mock.json` al primer inicio.

## Tests
- Ejecuta los tests unitarios e instrumentados desde Android Studio.

## Contacto
- compufire@ejemplo.com
Resumen

Esta rama añade integración de extremo a extremo con n8n mediante webhook protegido por HMAC-SHA256 para la función "Reservar / Consultar" desde DetailFragment. Arquitectura existente (Java, MVVM, Room, Retrofit/OkHttp/Gson) respetada.

Cambios principales (archivos nuevos/actualizados)

Red (n8n)

app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/remote/booking/BookingRequest.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/remote/booking/BookingResponse.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/remote/booking/HmacUtil.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/remote/booking/N8nApi.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/remote/booking/N8nRepository.java
Persistencia (Habitación)

app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/local/booking/BookingHistory.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/local/booking/BookingHistoryDao.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/local/db/AppDatabase.java (entidad agregada BookingHistory, versión -> 2, fallbackToDetainingMigration())
Interfaz de usuario

res/layout/fragment_detail.xml (se agregó Button btnBook y ProgressBar)
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/ui/detail/DetailFragment.java (handler: DatePicker + TimePicker, construcción de BookingRequest, llamada síncrona en segundo plano a N8nRepository, Progress UI, persistencia en Room, Snackbar con acción "Ver historial")
res/diseño/fragmento_historial_de_reservas.xml
res/diseño/historial_de_reservas_de_artículos.xml
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/ui/booking/BookingHistoryFragment.java
app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/ui/booking/BookingHistoryAdapter.java
res/navigation/nav_graph.xml (se añadió bookingHistoryFragment)
Util

app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/util/Iso8601Utils.java
Recursos

res/drawable/ic_baseline_check_circle_24.xml
res/drawable/ic_baseline_error_outline_24.xml (añadido)
Gradle

app/build.gradle.kts: expone BuildConfig.N8N_BASE_URL y BuildConfig.SHARED_SECRET desde local.properties
Cómo configurar local.properties (NO versionar)

Añade en local.properties:

N8N_BASE_URL=https://<tu_subdominio>.n8n.cloud SHARED_SECRET=<tu_secret_compartido> GEMINI_API_KEY=<tu_gemini_key (si aplica)>

Luego: Sincronizar proyecto con Gradle (Android Studio) para generar BuildConfig con las variables.

Pruebas (pasos)

Caso feliz
Abrir aplicación → Inicio → Artículo → Detalle
Pulsar "Reservar / Consultar"
Seleccionar fecha y hora
Ver ProgressBar y luego Snackbar con mensaje "Estado: CONFIRMADA • Código:" (ejemplo)

Pulsar "Ver historial" → Historial debe mostrar la fila con fecha, businessName, status y code

Ver en DB que booking_history contiene el registro (AppDatabase)



Respuestas PENDIENTE / RECHAZADA



n8n puede devolver status diferentes; se mostrarán en Snackbar y se persistirán.



Firma inválida



Si la firma X-Signature no coincide, n8n debe responder 4xx/401; la app mostrará error claro (mensaje HTTP ...) y se registrará intento con status "ERROR" o código "ERR" según la respuesta.



Timeout / 5xx



N8nRepository lanza IOException con texto, la UI muestra Snackbar con error. El intento también se guarda en la tabla booking_history.


Comando cURL de ejemplo (para probar desde consola)


SECRET="<tu_secret>"
BODY='{"requestId":"demo-1","user":{"id":"u1","name":"Angel","email":"acermenog@miumg.edu.gt"},"business":{"id":"b1","name":"Tacos"},"type":"RESERVA","datetime":"2025-11-05T15:30:00-06:00","notes":"ventana"}'
SIG=$(echo -n "$BODY" | openssl dgst -sha256 -hmac "$SECRET" | sed 's/^.* //')
curl -X POST "https://<tu_subdominio>.n8n.cloud/webhook/recojutiapa/booking" 
-H "Content-Type: application/json" 
-H "X-Signature: $SIG" 
-d "$BODY"


Notas y supuestos



Usuario por defecto para pruebas: id="u_1", name="Angel", email="acermenog@miumg.edu.gt". Puedes reemplazar por UserPrefs si ya está implementado.

Iso8601Utils usa SimpleDateFormat para compatibilidad con minSdk 24.

N8nRepository usa reflexión para leer BuildConfig.* y proporciona fallback mínimo; preferible sincronizar gradle para generar BuildConfig correctamente.

AppDatabase incrementó versión a 2; se usa fallbackToDestructiveMigration() para desarrollo.

No se introdujo Kotlin ni librerías adicionales.


Siguientes pasos recomendados



Sincronizar Gradle en Android Studio para que BuildConfig incluya N8N_BASE_URL y SHARED_SECRET.

Ejecutar proyecto y verificar permisos/Network.

Probar con cURL y con n8n configurado.


Si desea, puedo:



Resolver advertencias de internacionalización (mover strings a resources).

Añadir menú para acceder al Historial desde MainActivity.

Ajustar UI/estilos del RecyclerView.


Archivos relevantes listos para pegar ya están en el repositorio.
