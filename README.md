# TFG - Desarrollo de una Aplicación Móvil para la Organización de Partidos y Torneos Deportivos

## Memoria del Proyecto

En este documento se recoge la memoria del **Trabajo Fin de Grado (TFG)** realizado por Ignacio Canículo Domínguez, destinado a la finalización de los estudios del Grado en Ingeniería del Software de la Universidad Rey Juan Carlos.

```bibtex
@mastersthesis{tfg2025,
  title = {Desarrollo de una Aplicación Móvil para la Organización de Partidos y Torneos Deportivos},
  author = {Canículo Domínguez, I. and Cavero, S.},
  school = {E.T.S. de Ingeniería Informática},
  year = {2025},
  month = {6},
  type = {Trabajo Fin de Grado},
  url = {https://github.com/nachocaniculo/TFG},
}
```

---

## Resumen

El objetivo de este Trabajo Fin de Grado es desarrollar una aplicación móvil para la organización de partidos y torneos deportivos, con un enfoque principal en deportes populares como el pádel, baloncesto y fútbol, pero adaptable a otras disciplinas deportivas.

Algunas de las principales funcionalidades son la posibilidad de formar equipos, organizar partidos, reservar pistas y administrar la inscripción en torneos. Además, los jugadores pueden valorar a sus rivales de forma anónima, así como consultar las clasificaciones de los torneos.

La aplicación desarrollada está diseñada para optimizar la experiencia de organización y participación en actividades deportivas tanto de forma recreativa como competitiva, promoviendo la interacción entre distintos jugadores y facilitando la gestión de una forma eficiente. Por otro lado, cuenta con una interfaz intuitiva, pensada para que los usuarios puedan utilizarla de manera sencilla y fluida.

Esta aplicación se implementa mediante el lenguaje de programación **Kotlin** y se utiliza una base de datos en **Firebase** para gestionar los eventos y los usuarios. Finalmente, se realizan pruebas para validar las funcionalidades de la aplicación y detectar posibles vías de mejora.

---

## Estructura de la Aplicación

La aplicación está organizada en los siguientes módulos principales:

- **Autenticación de Usuarios:** Registro, inicio de sesión y gestión de perfiles.
- **Gestión de Equipos:** Creación, edición y administración de equipos deportivos.
- **Organización de Partidos:** Planificación, reserva de pistas y notificaciones.
- **Gestión de Torneos:** Inscripción de equipos, generación de cuadros y seguimiento de clasificaciones.
- **Valoraciones Anónimas:** Sistema de puntuación y comentarios entre jugadores.
- **Interfaz de Usuario:** Navegación sencilla e intuitiva optimizada para dispositivos móviles.
- **Base de Datos:** Gestión de datos en tiempo real mediante Firebase.

---

## Tecnologías Utilizadas

- **Lenguaje de Programación:** Kotlin
- **Plataforma:** Android
- **Base de Datos:** Firebase Realtime Database / Firestore
- **Herramientas de Desarrollo:** Android Studio, Gradle
- **Otras Tecnologías:** Notificaciones Push, Autenticación Firebase

---

## Instalación y Uso

1. **Clona el repositorio:**
   ```sh
   git clone https://github.com/nachocaniculo/TFG.git
   ```
2. **Abre el proyecto en Android Studio.**
3. **Configura Firebase:**
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
   - Descarga el archivo `google-services.json` y colócalo en el directorio `app/`.
   - Configura las reglas de la base de datos y autenticación según la documentación de Firebase.
4. **Compila y ejecuta la aplicación en un emulador o dispositivo Android.**

---

## Documentación

- El código fuente se encuentra organizado en carpetas según los distintos módulos funcionales.
- Archivos y carpetas principales:
  - `app/` - Código fuente de la aplicación Android.
  - `README.md` - Este documento.
  - `docs/` - Documentación adicional (si aplica).
  - `images/` - Recursos gráficos y capturas de pantalla.

Para más detalles sobre el uso y la estructura del proyecto, consulta los comentarios en el código y la documentación técnica incluida en el directorio `docs/`.

---

## Licencia

La aplicación PlayConnect, desarrollada como parte de este Trabajo de Fin de Grado, se encuentra actualmente protegida bajo una licencia privativa con todos los derechos reservados. Esto implica que no se permite la copia, modificación, distribución o uso del código fuente, ya sea total o parcial, sin el consentimiento expreso del autor.

---

## Contacto

- Ignacio Canículo Domínguez  
- Email: [nachocaniculo@gmail.com]  
- Universidad Rey Juan Carlos
