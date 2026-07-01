package negocio;

import modelo.Docente;
import modelo.Ejercicio;
import modelo.Materia;
import modelo.Usuario;
import negocio.excepciones.AutorizacionException;
import negocio.excepciones.DatosInvalidosException;

import java.util.ArrayList;
import java.util.List;

/**
 * MODULO 3 - Banco Adaptativo de Ejercicios (Mateo Garzon).
 * CONTROLADOR que administra el banco, valida las respuestas en tiempo
 * real y ajusta dinamicamente la dificultad del siguiente ejercicio.
 *
 * Reglas de negocio:
 *   - AUTORIZACION (RF3.1): solo un Docente puede registrar ejercicios
 *     en el banco. Un Estudiante recibe AutorizacionException.
 *   - VALIDACION: un ejercicio requiere enunciado, solucion, tema y un
 *     nivel de dificultad valido (BAJO/MEDIO/ALTO).
 *   - AJUSTE ADAPTATIVO (RF3.2): 3 aciertos consecutivos suben el nivel;
 *     2 fallos consecutivos lo bajan.
 */
public class GestorEjercicios {

    private final List<Ejercicio> banco;
    private int aciertosConsecutivos;
    private int fallosConsecutivos;
    private int totalRespuestas;
    private int totalAciertos;

    public GestorEjercicios() {
        this.banco = new ArrayList<>();
        this.aciertosConsecutivos = 0;
        this.fallosConsecutivos = 0;
        this.totalRespuestas = 0;
        this.totalAciertos = 0;
    }

    /**
     * Registra un ejercicio en el banco. Regla de AUTORIZACION: el
     * solicitante debe ser un Docente. Regla de VALIDACION: campos
     * obligatorios y nivel valido.
     *
     * @param solicitante usuario que intenta registrar (debe ser Docente)
     * @param e           ejercicio a registrar
     * @throws AutorizacionException  si el solicitante no es docente
     * @throws DatosInvalidosException si faltan campos o el nivel es invalido
     */
    public void registrarEjercicio(Usuario solicitante, Ejercicio e)
            throws AutorizacionException, DatosInvalidosException {
        if (!(solicitante instanceof Docente)) {
            throw new AutorizacionException(
                    "Operacion no permitida: solo un docente puede registrar ejercicios en el banco.");
        }
        if (e == null) {
            throw new DatosInvalidosException("El ejercicio es nulo.");
        }
        if (esVacio(e.getEnunciado())) {
            throw new DatosInvalidosException("El enunciado del ejercicio es obligatorio.");
        }
        if (esVacio(e.getSolucionCorrecta())) {
            throw new DatosInvalidosException("La solucion correcta es obligatoria.");
        }
        if (esVacio(e.getTema())) {
            throw new DatosInvalidosException("El tema del ejercicio es obligatorio.");
        }
        if (!nivelValido(e.getNivelDificultad())) {
            throw new DatosInvalidosException("El nivel debe ser BAJO, MEDIO o ALTO.");
        }
        banco.add(e);
    }

    /**
     * Evalua la respuesta del estudiante y actualiza las rachas de
     * aciertos/fallos que alimentan el ajuste adaptativo.
     */
    public boolean evaluarRespuesta(Ejercicio e, String respuesta) {
        if (e == null) return false;
        totalRespuestas++;
        boolean correcta = e.verificarRespuesta(respuesta);
        if (correcta) {
            aciertosConsecutivos++;
            fallosConsecutivos = 0;
            totalAciertos++;
        } else {
            fallosConsecutivos++;
            aciertosConsecutivos = 0;
        }
        return correcta;
    }

    /**
     * Determina el nivel recomendado para el proximo ejercicio segun la
     * regla adaptativa (3 aciertos suben, 2 fallos bajan).
     */
    public String calcularSiguienteDificultad(String nivelActual) {
        if (aciertosConsecutivos >= 3) {
            aciertosConsecutivos = 0;
            if (Ejercicio.DIFICULTAD_BAJO.equalsIgnoreCase(nivelActual)) return Ejercicio.DIFICULTAD_MEDIO;
            if (Ejercicio.DIFICULTAD_MEDIO.equalsIgnoreCase(nivelActual)) return Ejercicio.DIFICULTAD_ALTO;
        }
        if (fallosConsecutivos >= 2) {
            fallosConsecutivos = 0;
            if (Ejercicio.DIFICULTAD_ALTO.equalsIgnoreCase(nivelActual)) return Ejercicio.DIFICULTAD_MEDIO;
            if (Ejercicio.DIFICULTAD_MEDIO.equalsIgnoreCase(nivelActual)) return Ejercicio.DIFICULTAD_BAJO;
        }
        return nivelActual;
    }

    /** Filtra el banco por materia y/o nivel (ambos opcionales). */
    public List<Ejercicio> filtrar(Materia m, String nivel) {
        List<Ejercicio> resultado = new ArrayList<>();
        for (Ejercicio e : banco) {
            boolean coincideMateria = (m == null) || (e.getMateria() != null && e.getMateria().equals(m));
            boolean coincideNivel = (nivel == null) || nivel.equalsIgnoreCase(e.getNivelDificultad());
            if (coincideMateria && coincideNivel) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /** Reinicia las rachas y contadores para una nueva sesion de practica. */
    public void reiniciarSesion() {
        this.aciertosConsecutivos = 0;
        this.fallosConsecutivos = 0;
        this.totalRespuestas = 0;
        this.totalAciertos = 0;
    }

    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean nivelValido(String nivel) {
        return Ejercicio.DIFICULTAD_BAJO.equalsIgnoreCase(nivel)
                || Ejercicio.DIFICULTAD_MEDIO.equalsIgnoreCase(nivel)
                || Ejercicio.DIFICULTAD_ALTO.equalsIgnoreCase(nivel);
    }

    public List<Ejercicio> getBanco() { return banco; }
    public int getTotalRespuestas() { return totalRespuestas; }
    public int getTotalAciertos() { return totalAciertos; }

    public double getPorcentajeAciertos() {
        if (totalRespuestas == 0) return 0.0;
        return (totalAciertos * 100.0) / totalRespuestas;
    }
}
