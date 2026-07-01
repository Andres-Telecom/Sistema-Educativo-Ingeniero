# Sistema Educativo de Ingeniería UDLA
### Ruta Crítica Adaptativa en Ciencias Básicas — Programación II (2026)

Aplicación de escritorio en **Java (POO)** con **interfaz gráfica (Swing)** que diagnostica al
estudiante, genera una **ruta de estudio personalizada** con refuerzo de prerrequisitos, ofrece
**práctica adaptativa** y un **dashboard** con alertas tempranas de riesgo académico.

## Repositorio
```bash
git clone https://github.com/Andres-Telecom/Sistema-Educativo-Ingeniero.git
```

## Requisitos
- JDK 11 o superior (probado en JDK 11 y 21). La interfaz usa Swing, incluido en el JDK.

## Estructura (arquitectura MVC en capas)
```
src/
├── modelo/     (entidades de datos: Usuario, Estudiante, Docente, Materia, Ejercicio, ...)
├── negocio/    (reglas de negocio: GestorPerfil, GestorRutas, GestorEjercicios, GestorDashboard)
│   └── excepciones/ (ReglaNegocioException y subclases)
└── interfaz/   (interfaz gráfica Swing; clase principal AppSistemaEducativo)
```

## Cómo ejecutar
### En IntelliJ IDEA
1. File → Open → seleccionar la carpeta `SistemaEducativoIngenieria`.
2. Marcar `src` como *Sources Root* (clic derecho → Mark Directory as → Sources Root).
3. Ejecutar la clase `interfaz.AppSistemaEducativo`.

### Por terminal
```bash
javac -encoding UTF-8 -d out src/modelo/*.java src/negocio/excepciones/*.java src/negocio/*.java src/interfaz/*.java
java -cp out interfaz.AppSistemaEducativo
```

## Cuentas de prueba (precargadas)
| Rol | Correo | Contraseña |
|-----|--------|-----------|
| Estudiante | demo@udla.edu.ec | udla2024 |
| Docente | docente@udla.edu.ec | docente2024 |

Cédulas ecuatorianas válidas para registrar nuevos estudiantes: `1723456784`, `0101234565`, `1309876546`.

## Módulos y responsables
- **M1 Gestión de Perfil** (Maikel Cachimuel): registro/autenticación e índice de carga.
- **M2 Diagnóstico y Ruta** (Andrés Ugsha): evaluación y ruta con refuerzos.
- **M3 Banco Adaptativo** (Mateo Garzón): dificultad dinámica y autorización por rol.
- **M4 Dashboard** (David Galarza): avance, estado y alertas de riesgo.

## Documentación
En `docs/`: informe (PDF y DOCX), presentación (PPTX), diagrama UML y de arquitectura, y capturas.
