package modelo;

/**
 * Ejercicio individual del banco de problemas de ciencias basicas.
 * Cada ejercicio pertenece a una {@link Materia} (agregacion) y puede
 * incluirse en una {@link RutaCritica} (agregacion). Es la unidad
 * minima sobre la cual se evalua al estudiante.
 */
public class Ejercicio {

    /** Constantes de nivel de dificultad utilizadas en todo el sistema. */
    public static final String DIFICULTAD_BAJO = "BAJO";
    public static final String DIFICULTAD_MEDIO = "MEDIO";
    public static final String DIFICULTAD_ALTO = "ALTO";

    private int idEjercicio;
    private String tema;
    private String nivelDificultad; // BAJO / MEDIO / ALTO
    private String enunciado;
    private String solucionCorrecta;
    private Materia materia;

    public Ejercicio() {
    }

    public Ejercicio(int idEjercicio, String tema, String nivelDificultad,
                     String enunciado, String solucionCorrecta, Materia materia) {
        this.idEjercicio = idEjercicio;
        this.tema = tema;
        this.nivelDificultad = nivelDificultad;
        this.enunciado = enunciado;
        this.solucionCorrecta = solucionCorrecta;
        this.materia = materia;
    }

    /**
     * Compara la respuesta del estudiante con la solucion correcta.
     *
     * Regla de negocio (validacion robusta, "no facilismo"):
     *   1) Si ambas cadenas son numeros, se comparan como valores con
     *      tolerancia 1e-6 (asi "2" y "2.0" se consideran iguales y se
     *      acepta la coma decimal).
     *   2) En caso contrario, se comparan como texto ignorando
     *      mayusculas/minusculas y TODOS los espacios (para respuestas
     *      simbolicas como "cos(x^2)*2x").
     *
     * @param respuesta texto ingresado por el estudiante
     * @return true si la respuesta es correcta
     */
    public boolean verificarRespuesta(String respuesta) {
        if (respuesta == null || solucionCorrecta == null) {
            return false;
        }
        String r = respuesta.trim();
        String s = solucionCorrecta.trim();
        if (r.isEmpty()) {
            return false;
        }
        try {
            double dr = Double.parseDouble(r.replace(",", "."));
            double ds = Double.parseDouble(s.replace(",", "."));
            return Math.abs(dr - ds) < 1e-6;
        } catch (NumberFormatException ex) {
            return normalizar(r).equalsIgnoreCase(normalizar(s));
        }
    }

    /** Elimina todos los espacios internos para comparar respuestas simbolicas. */
    private String normalizar(String x) {
        return x.replaceAll("\\s+", "");
    }

    /**
     * Ajusta el nivel de dificultad del ejercicio en funcion de la racha
     * de aciertos/fallos consecutivos del estudiante.
     *   - 3 o mas aciertos consecutivos suben el nivel un paso.
     *   - 2 o mas fallos consecutivos lo bajan un paso.
     *
     * @param aciertos racha de aciertos (negativa = fallos consecutivos)
     */
    public void ajustarDificultadDinamica(int aciertos) {
        if (aciertos >= 3) {
            if (DIFICULTAD_BAJO.equalsIgnoreCase(this.nivelDificultad)) {
                this.nivelDificultad = DIFICULTAD_MEDIO;
            } else if (DIFICULTAD_MEDIO.equalsIgnoreCase(this.nivelDificultad)) {
                this.nivelDificultad = DIFICULTAD_ALTO;
            }
        } else if (aciertos <= -2) {
            if (DIFICULTAD_ALTO.equalsIgnoreCase(this.nivelDificultad)) {
                this.nivelDificultad = DIFICULTAD_MEDIO;
            } else if (DIFICULTAD_MEDIO.equalsIgnoreCase(this.nivelDificultad)) {
                this.nivelDificultad = DIFICULTAD_BAJO;
            }
        }
    }

    @Override
    public String toString() {
        return "[" + nivelDificultad + "] " + tema + ": " + enunciado;
    }

    // Getters y setters
    public int getIdEjercicio() { return idEjercicio; }
    public void setIdEjercicio(int idEjercicio) { this.idEjercicio = idEjercicio; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(String nivelDificultad) { this.nivelDificultad = nivelDificultad; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getSolucionCorrecta() { return solucionCorrecta; }
    public void setSolucionCorrecta(String solucionCorrecta) { this.solucionCorrecta = solucionCorrecta; }

    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }
}
