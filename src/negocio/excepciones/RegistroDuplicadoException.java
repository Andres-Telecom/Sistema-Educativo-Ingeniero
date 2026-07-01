package negocio.excepciones;

/** Se lanza cuando se intenta registrar un usuario con una cedula o
 *  correo que ya existe en el sistema (unicidad de identidad). */
public class RegistroDuplicadoException extends ReglaNegocioException {
    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
