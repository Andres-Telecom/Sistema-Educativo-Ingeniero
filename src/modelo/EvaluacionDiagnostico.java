package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Evaluacion diagnostica inicial que realiza el estudiante al comenzar
 * una materia de ciencias basicas. Almacena las preguntas seleccionadas
 * y el puntaje global obtenido. Alimenta a la {@link RutaCritica} con
 * los datos para construir el plan personalizado de estudio
 * (dependencia EvaluacionDiagnostico -> RutaCritica).
 */
public class EvaluacionDiagnostico {

    private int idEvaluacion;
    private Date fechaRealizacion;
    private double puntajeGlobal;
    private List<Ejercicio> preguntas;
    private Estudiante estudiante;

    public EvaluacionDiagnostico() {
        this.preguntas = new ArrayList<>();
        this.fechaRealizacion = new Date();
    }

    public EvaluacionDiagnostico(int idEvaluacion, Estudiante estudiante) {
        this.idEvaluacion = idEvaluacion;
        this.estudiante = estudiante;
        this.fechaRealizacion = new Date();
        this.puntajeGlobal = 0.0;
        this.preguntas = new ArrayList<>();
    }

    /**
     * Selecciona aleatoriamente un subconjunto de ejercicios del banco
     * para construir la evaluacion diagnostica.
     *
     * @param banco    lista completa de ejercicios disponibles
     * @param cantidad numero de preguntas a incluir
     * @return lista de ejercicios seleccionados aleatoriamente
     */
    public List<Ejercicio> generarPreguntasAleatorias(List<Ejercicio> banco, int cantidad) {
        if (banco == null || banco.isEmpty()) {
            return new ArrayList<>();
        }
        List<Ejercicio> copia = new ArrayList<>(banco);
        Collections.shuffle(copia);
        int limite = Math.min(cantidad, copia.size());
        this.preguntas = new ArrayList<>(copia.subList(0, limite));
        return this.preguntas;
    }

    /**
     * Evalua las respuestas del estudiante contra las soluciones
     * correctas y calcula el puntaje global en escala 0-100.
     *
     * @param respuestas respuestas en el mismo orden que las preguntas
     */
    public void evaluarConocimientos(List<String> respuestas) {
        if (preguntas == null || preguntas.isEmpty() || respuestas == null) {
            this.puntajeGlobal = 0.0;
            return;
        }
        int aciertos = 0;
        int total = Math.min(preguntas.size(), respuestas.size());
        for (int i = 0; i < total; i++) {
            if (preguntas.get(i).verificarRespuesta(respuestas.get(i))) {
                aciertos++;
            }
        }
        this.puntajeGlobal = (aciertos * 100.0) / preguntas.size();
    }

    // Getters y setters
    public int getIdEvaluacion() { return idEvaluacion; }
    public void setIdEvaluacion(int idEvaluacion) { this.idEvaluacion = idEvaluacion; }

    public Date getFechaRealizacion() { return fechaRealizacion; }
    public void setFechaRealizacion(Date fechaRealizacion) { this.fechaRealizacion = fechaRealizacion; }

    public double getPuntajeGlobal() { return puntajeGlobal; }
    public void setPuntajeGlobal(double puntajeGlobal) { this.puntajeGlobal = puntajeGlobal; }

    public List<Ejercicio> getPreguntas() { return preguntas; }
    public void setPreguntas(List<Ejercicio> preguntas) { this.preguntas = preguntas; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
}
