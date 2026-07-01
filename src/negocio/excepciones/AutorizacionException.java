package negocio.excepciones;

/** Se lanza cuando un usuario intenta ejecutar una operacion para la
 *  que su rol no tiene permiso (p. ej. un estudiante intentando
 *  registrar ejercicios en el banco). */
public class AutorizacionException extends ReglaNegocioException {
    public AutorizacionException(String mensaje) {
        super(mensaje);
    }
}
