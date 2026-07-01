package negocio.excepciones;

/** Se lanza cuando un dato de entrada no cumple las reglas de validacion
 *  (cedula, correo, contrasena, semestre, campos vacios, etc.). */
public class DatosInvalidosException extends ReglaNegocioException {
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
