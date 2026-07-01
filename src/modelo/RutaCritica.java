package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Ruta personalizada de estudio que el sistema genera para un
 * estudiante a partir de su evaluacion diagnostica. Mantiene una
 * secuencia ordenada de ejercicios (agregacion RutaCritica <>- Ejercicio)
 * y permite insertar refuerzos cuando se detectan prerrequisitos no
 * dominados.
 */
public class RutaCritica {

    private int idRuta;
    private boolean estadoCompletado;
    private List<Ejercicio> secuencia;
    private Estudiante estudiante;

    public RutaCritica() {
        this.secuencia = new ArrayList<>();
        this.estadoCompletado = false;
    }

    public RutaCritica(int idRuta, Estudiante estudiante) {
        this.idRuta = idRuta;
        this.estudiante = estudiante;
        this.estadoCompletado = false;
        this.secuencia = new ArrayList<>();
    }

    /**
     * Genera la secuencia ordenada de ejercicios a partir de los
     * resultados de la evaluacion diagnostica (punto de partida de la
     * ruta).
     *
     * @param evaluacion evaluacion diagnostica del estudiante
     */
    public void generarSecuencia(EvaluacionDiagnostico evaluacion) {
        if (evaluacion == null || evaluacion.getPreguntas() == null) {
            return;
        }
        this.secuencia.clear();
        for (Ejercicio e : evaluacion.getPreguntas()) {
            this.secuencia.add(e);
        }
        this.estadoCompletado = false;
    }

    /**
     * Inserta un refuerzo de prerrequisito al INICIO de la ruta cuando
     * el estudiante presenta deficiencias en una materia que requiere
     * conocimientos previos. Cada prerrequisito se transforma en un
     * paso de repaso ubicado antes del bloque principal.
     *
     * @param m materia cuyos prerrequisitos deben reforzarse
     */
    public void insertarRefuerzoPrerrequisito(Materia m) {
        if (m == null || m.obtenerPrerrequisitos() == null) {
            return;
        }
        for (Materia prereq : m.obtenerPrerrequisitos()) {
            Ejercicio refuerzo = new Ejercicio(
                    9000 + secuencia.size(),
                    "Refuerzo: " + prereq.getNombre(),
                    Ejercicio.DIFICULTAD_BAJO,
                    "Repaso de conceptos base de " + prereq.getNombre(),
                    "OK",
                    prereq
            );
            this.secuencia.add(0, refuerzo);
        }
    }

    public void marcarComoCompletado() {
        this.estadoCompletado = true;
    }

    // Getters y setters
    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public boolean isEstadoCompletado() { return estadoCompletado; }
    public void setEstadoCompletado(boolean estadoCompletado) { this.estadoCompletado = estadoCompletado; }

    public List<Ejercicio> getSecuencia() { return secuencia; }
    public void setSecuencia(List<Ejercicio> secuencia) { this.secuencia = secuencia; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
}
