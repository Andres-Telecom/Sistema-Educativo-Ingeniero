package negocio;

import modelo.Ejercicio;
import modelo.Estudiante;
import modelo.EvaluacionDiagnostico;
import modelo.Materia;
import modelo.RutaCritica;
import negocio.excepciones.DatosInvalidosException;

import java.util.List;

/**
 * MODULO 2 - Motor de Evaluacion Diagnostica y Generacion de Rutas
 * (Andres Ugsha). CONTROLADOR que coordina la aplicacion del
 * diagnostico y la construccion de la RutaCritica personalizada.
 *
 * Regla de negocio (RF2.2): si el puntaje global del diagnostico cae
 * por debajo del UMBRAL_REFUERZO, inserta automaticamente al inicio de
 * la ruta los prerrequisitos de la materia principal, rompiendo el
 * ciclo de fracaso acumulativo tipico de las materias filtro.
 */
public class GestorRutas {

    /** Umbral bajo el cual se insertan refuerzos de prerrequisitos. */
    public static final double UMBRAL_REFUERZO = 60.0;

    /**
     * Aplica la evaluacion diagnostica: genera preguntas aleatorias del
     * banco y califica las respuestas del estudiante.
     *
     * @throws DatosInvalidosException si faltan datos o el banco esta vacio
     */
    public EvaluacionDiagnostico aplicarDiagnostico(Estudiante estudiante,
                                                    List<Ejercicio> bancoEjercicios,
                                                    List<String> respuestas,
                                                    int cantidad) throws DatosInvalidosException {
        if (estudiante == null) {
            throw new DatosInvalidosException("No hay un estudiante activo para el diagnostico.");
        }
        if (bancoEjercicios == null || bancoEjercicios.isEmpty()) {
            throw new DatosInvalidosException("El banco de ejercicios esta vacio.");
        }
        EvaluacionDiagnostico ev = new EvaluacionDiagnostico(1, estudiante);
        ev.generarPreguntasAleatorias(bancoEjercicios, cantidad);
        ev.evaluarConocimientos(respuestas);
        return ev;
    }

    /**
     * Prepara las preguntas del diagnostico SIN calificar todavia (para
     * que la interfaz las muestre una a una y recoja las respuestas).
     */
    public EvaluacionDiagnostico prepararDiagnostico(Estudiante estudiante,
                                                     List<Ejercicio> bancoEjercicios,
                                                     int cantidad) throws DatosInvalidosException {
        if (estudiante == null) {
            throw new DatosInvalidosException("No hay un estudiante activo para el diagnostico.");
        }
        if (bancoEjercicios == null || bancoEjercicios.isEmpty()) {
            throw new DatosInvalidosException("El banco de ejercicios esta vacio.");
        }
        EvaluacionDiagnostico ev = new EvaluacionDiagnostico(1, estudiante);
        ev.generarPreguntasAleatorias(bancoEjercicios, cantidad);
        return ev;
    }

    /**
     * Genera la ruta critica personalizada a partir del diagnostico ya
     * calificado. Inserta refuerzos si el puntaje es bajo (RF2.2).
     */
    public RutaCritica generarRuta(Estudiante estudiante,
                                   EvaluacionDiagnostico evaluacion,
                                   Materia materiaPrincipal) throws DatosInvalidosException {
        if (evaluacion == null) {
            throw new DatosInvalidosException("No hay una evaluacion diagnostica valida.");
        }
        RutaCritica ruta = new RutaCritica(1, estudiante);
        ruta.generarSecuencia(evaluacion);
        if (evaluacion.getPuntajeGlobal() < UMBRAL_REFUERZO && materiaPrincipal != null) {
            ruta.insertarRefuerzoPrerrequisito(materiaPrincipal);
        }
        return ruta;
    }
}
