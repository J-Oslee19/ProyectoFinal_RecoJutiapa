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

