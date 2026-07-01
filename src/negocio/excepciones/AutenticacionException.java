package negocio.excepciones;

/** Se lanza cuando las credenciales de acceso son incorrectas o el
 *  usuario no existe. */
public class AutenticacionException extends ReglaNegocioException {
    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
